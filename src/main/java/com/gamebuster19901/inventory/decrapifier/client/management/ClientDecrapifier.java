package com.gamebuster19901.inventory.decrapifier.client.management;


import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIConfig;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler;
import com.gamebuster19901.inventory.decrapifier.common.CommonDecrapifier;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ClientResponsePacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerAskPickupItemPacket;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;


/*
 * TODO:
 * Is this a god object? Should I break this up into multiple classes?
 */

public final class ClientDecrapifier extends CommonDecrapifier{
	private static final Field isGlowing = ReflectionHelper.findField(Entity.class, new String[]{"glowing", "field_184238_ar"});
	private static final Field pickupDelay = ReflectionHelper.findField(EntityItem.class, new String[]{"pickupDelay", "field_145804_b"});
	private final ItemStack[] inv = new ItemStack[36];
	private final ItemStack[] hot = new ItemStack[9];
	private EntityItem target;
	private boolean pickupTarget = false;
	double maxDistance = 0;
	
	private long TimeLastDeathMessage = 0;
	
	@SubscribeEvent
	public final void onPlayerTick(TickEvent.PlayerTickEvent e) throws IllegalArgumentException, IllegalAccessException{
		if(e.player == Minecraft.getMinecraft().player) {
			if(e.phase == TickEvent.Phase.START){
				if(e.player.world.isRemote){
					EntityItem newTarget = getEntityItemPlayerIsLookingAt(e.player, 5);
					if(newTarget != null && e.player.getDistance(newTarget.posX, newTarget.posY, newTarget.posZ) <= 1.4d){
						setGlowingItem(newTarget);
					}
					else{
						setGlowingItem(null);
					}
					if (newTarget != null){
						Blacklist.INSTANCE.contains(newTarget.getItem());
					}
					if(Minecraft.getMinecraft().gameSettings.isKeyDown(ClientProxy.getKeyBindings()[2])){
						dropBlacklistedItems();
					}
				}
			}
		}
	}
	
	/*
	 * Only make the item glow under the following conditions
	 * 
	 * 1. Highlight Items is ON
	 * AND
	 * 2. Target is new
	 * AND
	 * 		pickupItemsByDefault is FALSE
	 * 		OR
	 * 		the BlackList is ON AND the blacklist contains the item
	 * 
	 */
	private final void setGlowingItem(EntityItem item){
		if (item != null){
			if (!pickupItemsByDefault() || (blacklistEnabled() && Blacklist.INSTANCE.contains(item.getItem()))){
				setGlowing(target, false);
				target = item;
				setGlowing(target, highlightEnabled());
				return;
			}
		}
		else {
			setGlowing(target, false);
			target = null;
		}
	}
	
	private final void setGlowing(EntityItem e, boolean state){
		if (e != null){
			try{
				for(int i = 0; i < 100; i++){
					isGlowing.set(e, state);
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e1){
				throw new RuntimeException(e1);
			}
		}
	}
	
	@SubscribeEvent
	public final void EveryTick(ClientTickEvent e){
		processBindings();
	}
	
	private final void processBindings(){
		KeyBinding[] bindings = ((ClientProxy)Main.Proxy).getKeyBindings();
		for(int i = 0; i < bindings.length; i++) {
			processBinding(i, bindings[i].isKeyDown(), bindings[i].isPressed());
		}
	}
	
	private final void processBinding(int index, boolean state, boolean justPressed) {
		EntityPlayer p = Minecraft.getMinecraft().player;
		switch(index) {
			case 0:
				if(justPressed) {
					pickupTarget = justPressed;
				}
				break;
			case 1:
				if(justPressed) {
					p.openGui(Main.getInstance(), GUIHandler.GUI_BLACKLIST, p.world, (int)p.posX, (int)p.posY, (int)p.posZ);
				}
				break;
			case 2:
				if(justPressed) {
					dropBlacklistedItems();
				}
				break;
			case 4:
				if(justPressed) {
					boolean newBlacklistState = !blacklistEnabled();
					String message = "§e[" + Main.MODNAME + "]:§r " + I18n.format("invdecrap.message.blacklist");
					message = newBlacklistState ? message + " §a§l" + I18n.format("addServer.resourcePack.enabled") : message + " §c§l" + I18n.format("addServer.resourcePack.disabled");
					GUIConfig.blacklistEnabled.set(newBlacklistState);
					GUIConfig.CONFIG.save();
					p.sendMessage(new TextComponentString(message));
				}
			default:
				return;
		}
	}
	
	public final ClientResponsePacket canPickup(ServerAskPickupItemPacket query) {
		boolean shouldPickup = false;
		if(target != null && query.getUUID().equals(target.getUniqueID()) && pickupTarget) {
			shouldPickup = true;
			pickupTarget = false;
			Main.LOGGER.log(Level.INFO, 1);
		}
		else if(pickupItemsByDefault() && !blacklistEnabled()) {
			shouldPickup = true;
			Main.LOGGER.log(Level.INFO, 2);
		}
		else if(pickupItemsByDefault() && blacklistEnabled() && !Blacklist.INSTANCE.contains(query.getItem())){
			shouldPickup = true;
			Main.LOGGER.log(Level.INFO, 3);
		}
		else if(target == null) {
			pickupTarget = false;
			Main.LOGGER.log(Level.INFO, 4);
		}
		Main.LOGGER.log(Level.INFO, 5);
		return new ClientResponsePacket(query, shouldPickup);
	}
	
/*	@SubscribeEvent
	@Override
	public final void onPickup(EntityItemPickupEvent e){
		debug(maxDistance);
		EntityItem entity = e.getItem();
		if((target != null && target.equals(entity) && pickupTarget) || (pickupItemsByDefault() && !blacklistEnabled()) || (pickupItemsByDefault() && blacklistEnabled() && !BannedItems.contains(((EntityItem)entity).getItem()))){
			pickups.add(e.getItem());
			return;
		}
		else{
			debug((target != null) + " " + 1);
			if (target != null){
				debug(target.equals(entity) + " " + 2);
			}
			debug(pickupTarget + " " + 3);
		}
		e.setCanceled(true);
	}*/
	
	public void dropBlacklistedItems(){
		if (Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatOpen()) {
			return;
		}
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		if (p == null) {return;}
		if(p.openContainer instanceof ContainerPlayer){
			for(Slot s :p.inventoryContainer.inventorySlots){
				if (Blacklist.INSTANCE.contains(s.getStack())){
					dropItem(0, s.slotNumber);
				}
			}
		}
		else{
			if(controlPressed()) {
				int windowID = p.openContainer.windowId;
				for(Slot s : p.openContainer.inventorySlots){
					if(Blacklist.INSTANCE.contains(s.getStack())){
						dropItem(windowID, s.slotNumber);
					}
				}
			}
		}
	}
	
	private boolean controlPressed() {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}
	
	public void dropItem(int windowID, int slot){
		Minecraft.getMinecraft().playerController.windowClick(windowID, slot, 1, ClickType.THROW, Minecraft.getMinecraft().player);
	}
	
	public final void decrapifyUnmodded(EntityPlayer p){

	}
	
	public final void decrapify(EntityPlayer p){
		if (p instanceof EntityPlayerMP){
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			if (p.openContainer != null){
				decrapify(p, p.openContainer);
			}
			items.addAll(p.inventory.mainInventory);
			decrapify(p, items, p.inventory.mainInventory.toArray(new ItemStack[0]));
			items.clear();
			items.addAll(p.inventory.offHandInventory);
			decrapify(p, items, p.inventory.offHandInventory.toArray(new ItemStack[0]));
			items.clear();
			items.addAll(p.inventory.armorInventory);
			decrapify(p, items, p.inventory.armorInventory.toArray(new ItemStack[0]));
		}
	}
	
	public final void decrapify(EntityPlayer p, Container c){
		for(int i = 0; i < c.inventoryItemStacks.size(); i++){
			ItemStack item = c.inventoryItemStacks.get(i);
			if (item != null && item.getItem() != null){
				c.inventoryItemStacks.set(i, (ItemStack) null);
				p.dropItem(item, true);
			}
		}
	}
	

	
	public final void decrapify(EntityPlayer p, ArrayList<ItemStack> items, ItemStack[] container){
		int j = 0;
		for(ItemStack i : items){
			if (i != null && i.getItem() != null){
				if(Blacklist.INSTANCE.contains(i)){
					container[j] = null;
					p.dropItem(i, true);
				}
			}
			j++;
		}
	}
	
	public static final boolean blacklistEnabled(){
		//debug("blacklist is " + ClientProxy.getConfig().get("Main", "isBlackListOn", false, "If true, you will not pickup anything in the blacklist, unless you press the 'Pick up Item' key").getBoolean());
		return GUIConfig.blacklistEnabled.getBoolean();
	}
	
	public static final boolean highlightEnabled(){
		//debug("Highlight is " + ClientProxy.getConfig().get("Main", "highlightSelectedItem", true, "If true, the item you are selecting for pickup will glow").getBoolean());
		return GUIConfig.highlightEnabled.getBoolean();
	}
	
	public static final boolean pickupItemsByDefault(){
		//debug("CanPickup is " + ClientProxy.getConfig().get("Main", "pickupItemsByDefault", true, "If false, you will not pick up ANYTHING unless you press the 'Pick up Item' key while looking at the item you want").getBoolean());
		return GUIConfig.pickupByDefault.getBoolean();
	}
	
	public static final NetHandlerPlayClient getConnection(){
		return Minecraft.getMinecraft().getConnection();
	}
}
	