package com.gamebuster19901.inventory.decrapifier.client.management;


import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler;
import com.gamebuster19901.inventory.decrapifier.common.CommonDecrapifier;
import com.gamebuster19901.inventory.decrapifier.common.PlayerPickupQueue;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ClientResponsePacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerAskPickupItemPacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerHasModHandler;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import io.netty.util.internal.MathUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
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
		if(e.player.world.isRemote){
			if(e.phase == TickEvent.Phase.START){
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
		switch(index) {
			case 0:
				if(justPressed) {
					pickupTarget = justPressed;
				}
				break;
			case 1:
				if(justPressed) {
					EntityPlayer p = Minecraft.getMinecraft().player;
					p.openGui(Main.getInstance(), GUIHandler.GUI_BLACKLIST, p.world, (int)p.posX, (int)p.posY, (int)p.posZ);
				}
				break;
			case 2:
				if(justPressed) {
					dropBlacklistedItems();
				}
				break;
			default:
				return;
		}
	}
	
	public final ClientResponsePacket canPickup(ServerAskPickupItemPacket query) {
		boolean shouldPickup = false;
		if(target != null && query.getUUID().equals(target.getUniqueID()) && pickupTarget) {
			shouldPickup = true;
			pickupTarget = false;
		}
		else if(pickupItemsByDefault() && !blacklistEnabled()) {
			shouldPickup = true;
		}
		else if(pickupItemsByDefault() && blacklistEnabled() && !Blacklist.INSTANCE.contains(query.getItem())){
			shouldPickup = true;
		}
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
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		if (p == null) {return;}
		if(p.openContainer == null){
			for(Slot s :p.inventoryContainer.inventorySlots){
				if (Blacklist.INSTANCE.contains(s.getStack())){
					dropItem(0, s.slotNumber);
				}
			}
		}
		else{
			int windowID = p.openContainer.windowId;
			for(Slot s : p.openContainer.inventorySlots){
				if(Blacklist.INSTANCE.contains(s.getStack())){
					dropItem(windowID, s.slotNumber);
				}
			}
		}
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
		return ClientProxy.getConfig().get("Main", "isBlackListOn", false, "If true, you will not pickup anything in the blacklist, unless you press the 'Pick up Item' key").getBoolean();
	}
	
	public static final boolean highlightEnabled(){
		//debug("Highlight is " + ClientProxy.getConfig().get("Main", "highlightSelectedItem", true, "If true, the item you are selecting for pickup will glow").getBoolean());
		return ClientProxy.getConfig().get("Main", "highlightSelectedItem", true, "If true, the item you are selecting for pickup will glow").getBoolean();
	}
	
	public static final boolean pickupItemsByDefault(){
		//debug("CanPickup is " + ClientProxy.getConfig().get("Main", "pickupItemsByDefault", true, "If false, you will not pick up ANYTHING unless you press the 'Pick up Item' key while looking at the item you want").getBoolean());
		return ClientProxy.getConfig().get("Main", "pickupItemsByDefault", true, "If false, you will not pick up ANYTHING unless you press the 'Pick up Item' key while looking at the item you want").getBoolean();
	}
	
	public static final NetHandlerPlayClient getConnection(){
		return Minecraft.getMinecraft().getConnection();
	}
}
	