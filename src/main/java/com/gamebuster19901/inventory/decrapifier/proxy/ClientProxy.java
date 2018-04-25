package com.gamebuster19901.inventory.decrapifier.proxy;

import static com.gamebuster19901.inventory.decrapifier.Main.LOGGER;
import static com.gamebuster19901.inventory.decrapifier.Main.MODID;
import static org.apache.logging.log4j.Level.WARN;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.events.listeners.ClientServerListener;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIAddToBlacklist;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.*;
import com.gamebuster19901.inventory.decrapifier.client.management.Blacklist;
import com.gamebuster19901.inventory.decrapifier.client.management.ClientDecrapifier;
import com.gamebuster19901.inventory.decrapifier.client.management.ListItem;
import com.gamebuster19901.inventory.decrapifier.server.ServerDecrapifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class ClientProxy extends Proxy{
	private static final KeyBinding[] KEYBINDINGS = new KeyBinding[]{
		new KeyBinding("key." + MODID + ".pickup.description", Keyboard.KEY_C, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".openblacklistgui.description", Keyboard.KEY_X, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".decrapify.description", Keyboard.KEY_B, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".quicktoss.description", Keyboard.KEY_V, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".quickblacklist.description", Keyboard.KEY_N, "key." + MODID + ".category")
	};
	
	static {
		KEYBINDINGS[0].setKeyConflictContext(KeyConflictContext.IN_GAME);
		KEYBINDINGS[1].setKeyConflictContext(KeyConflictContext.IN_GAME);
		KEYBINDINGS[2].setKeyConflictContext(KeyConflictContext.IN_GAME);
		KEYBINDINGS[3].setKeyConflictContext(KeyConflictContext.IN_GAME);
		KEYBINDINGS[4].setKeyConflictContext(KeyConflictContext.IN_GAME);
		Minecraft.getMinecraft().gameSettings.keyBindLoadToolbar.setKeyConflictContext(KeyConflictContext.GUI);
		Minecraft.getMinecraft().gameSettings.keyBindSaveToolbar.setKeyConflictContext(KeyConflictContext.GUI);
	}

	private static boolean doesServerContainMod;
	public static Configuration CONFIG;
	public static String SERVER_TYPE = "NONE";
	@Override
	public void preInit(FMLPreInitializationEvent e){
		DECRAP = new ClientDecrapifier();
		SERVER_DECRAP = new ServerDecrapifier();
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		setConfig(config);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClientServerListener());
		MinecraftForge.EVENT_BUS.register(new GUIBlacklist());
		super.preInit(e);
		syncToGUI();
	}
	
	@Override
	public void init(FMLInitializationEvent e){
		super.init(e);
		for(int i = 0; i < KEYBINDINGS.length; i++){
			ClientRegistry.registerKeyBinding(KEYBINDINGS[i]);
		}
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}
	
	@SubscribeEvent
	public final void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e){
		if(e.getModID().equals(MODID)){
			syncToGUI();
			getConfig().save();
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(RenderWorldLastEvent e){
		if(!flag){
			registerDependentGUIS();
		}
	}
	private boolean flag = false;
	private void registerDependentGUIS(){
		if (!flag){
			MinecraftForge.EVENT_BUS.register(new GUIAddToBlacklist());
			flag = !flag;
		}
		else{
			throw new IllegalStateException("Dependents already registered");
		}
	}
	
	private final void syncToGUI(){
		ArrayList<String> FailedIDs = new ArrayList<String>();
		ArrayList<String> FailedOre = new ArrayList<String>();
		String[] blackListID = getConfig().get("Main", "blackListedItemsByID", new String[]{"NULL"}, "A list of items (by ID) that you will not automatically pick up.\nThis only takes effect if the blacklist is on.").getStringList();
		String[] blackListOreDictionary = getConfig().get("Main", "blackListedItemsByOreDictionary", new String[]{"NULL"}, "A list of Ore Dictionary names. If an item corresponds with the Ore Dictionary name, you will not pick up the item. \n Only takes effect if the blacklist is on").getStringList();
		ClientDecrapifier decrap = (ClientDecrapifier) getDecrapifier();
		Blacklist blacklist = Blacklist.INSTANCE;
		blacklist.clearBannedItems();
		for(int i = 0; i < blackListID.length; i++){
			System.out.println(ListItem.fromString(blackListID[i], false));
			if (ListItem.fromString(blackListID[i], false) != null){
				blacklist.addToBlacklist(ListItem.fromString(blackListID[i], false));
				//decrap.addToBlacklist(new ItemStack(Item.getByNameOrId(blackListID[i])));
			}
			else{
				FailedIDs.add(blackListID[i]);
			}
		}
		for(int i = 0; i < blackListOreDictionary.length; i++){
			if(OreDictionary.doesOreNameExist(blackListOreDictionary[i])){
				blacklist.addToBlacklist(ListItem.fromString(blackListOreDictionary[i], true));
			}
			else{
				FailedOre.add(blackListOreDictionary[i]);
			}
		}
		if (FailedIDs.size() > 0){
			LOGGER.log(WARN, "The following item IDs could not be added to the blacklist because they do not exist:");
			for(String s : FailedIDs){
				LOGGER.log(WARN, "\"" + s + "\"");
			}
		}
		if (FailedOre.size() > 0){
			LOGGER.log(WARN, "The following OreDictionary values could not be added to the blacklist because they do not exist:");
			for(String s : FailedOre){
				LOGGER.log(WARN, "\"" + s + "\"");
			}
		}
	}
	
	public final void syncToFile(){
		ArrayList<String> blackListID = new ArrayList<String>();
		ArrayList<String> blackListOres = new ArrayList<String>();
		for(ListItem l: Blacklist.INSTANCE.getBannedItems()){
			if (l.isOre()){
				blackListOres.add(l.toString());
			}
			else{
				blackListID.add(l.toString());
			}
		}
		getConfig().getCategory("main").get("blackListedItemsByID").setValues(blackListID.toArray(new String[]{}));
		getConfig().getCategory("main").get("blackListedItemsByOreDictionary").setValues(blackListOres.toArray(new String[]{}));
		new ConfigChangedEvent(MODID, CONFIG.getConfigFile().getName(), true, false);
		getConfig().save();
	}
	
	public static final boolean serverContainsMod(){
		return doesServerContainMod;
	}
	
	public static final String getConnectionType(){
		return SERVER_TYPE;
	}
	
	public static final Configuration getConfig(){
		return CONFIG;
	}
	
	public static final KeyBinding[] getKeyBindings(){
		return KEYBINDINGS;
	}
	
	public static final void setConnectionType(String s){
		if (s.equals("MODDED") || s.equals("BUKKIT") || s.equals("VANILLA") || s.equals("NONE")){
			SERVER_TYPE = s;
			System.out.println("Connected to a " + SERVER_TYPE + " server");
			if(s.equals("VANILLA") || s.equals("NONE")){
				doesServerContainMod = false;
			}
			return;
		}
		throw new IllegalArgumentException(s + " is not valid");
	}
	
	private final static void config(){
		CONFIG.get("Main", "pickupItemsByDefault", true, "If false, you will not pick up ANYTHING unless you press the 'Pick up Item' key while looking at the item you want");
		CONFIG.get("Main", "isBlackListOn", false, "If true, you will not pickup anything in the blacklist, unless you press the 'Pick up Item' key");
		CONFIG.get("Main", "highlightSelectedItem", true, "If true, the item you are selecting for pickup will glow");
		CONFIG.get("Main", "blackListedItemsByID", new String[]{"NULL"}, "A list of items (by ID) that you will not automatically pick up.\nThis only takes effect if the blacklist is on.");
		CONFIG.get("Main", "blackListedItemsByOreDictionary", new String[]{"NULL"}, "A list of Ore Dictionary names. If an item corresponds with the Ore Dictionary name, you will not pick up the item. Only takes effect if the blacklist is on ");
		CONFIG.get("NonModServer", "tossBlackListedItemsOnPickup", true, "If the blacklist is on and this is true, then you will toss blacklisted items on pickup if you're not on a server with this mod installed " + TextFormatting.RED + " [NOT YET IMPLEMENTED]");
		CONFIG.save();
	}
	
	public static final void setConfig(Configuration config){
		if (CONFIG == null){
			CONFIG = config;
			config();
		}
		else{
			throw new IllegalStateException("Config variable already set!");
		}
	}
}
