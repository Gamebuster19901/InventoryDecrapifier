package com.gamebuster19901.inventory.decrapifier.proxy;

import static com.gamebuster19901.inventory.decrapifier.Main.MODID;
import org.lwjgl.input.Keyboard;

import com.gamebuster19901.inventory.decrapifier.client.events.listeners.ClientServerListener;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIAddToBlacklist;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIConfig;
import com.gamebuster19901.inventory.decrapifier.client.management.Blacklist;
import com.gamebuster19901.inventory.decrapifier.client.management.ClientDecrapifier;
import com.gamebuster19901.inventory.decrapifier.client.narrator.DisableNarrator;
import com.gamebuster19901.inventory.decrapifier.server.ServerDecrapifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends Proxy{
	private static final KeyBinding[] KEYBINDINGS = new KeyBinding[]{
		new KeyBinding("key." + MODID + ".pickup.description", Keyboard.KEY_C, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".openblacklistgui.description", Keyboard.KEY_X, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".decrapify.description", Keyboard.KEY_B, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".quickblacklist.description", Keyboard.KEY_V, "key." + MODID + ".category"),
		new KeyBinding("key." + MODID + ".toggleblacklist.description", Keyboard.KEY_N, "key." + MODID + ".category")
	};
	
	static {
		for(KeyBinding k : KEYBINDINGS) {
			k.setKeyConflictContext(KeyConflictContext.IN_GAME);
		}
		KEYBINDINGS[2].setKeyConflictContext(KeyConflictContext.UNIVERSAL);
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
		new DisableNarrator();
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
		Blacklist.updateBlacklistsFromConfig();
	}
	
	public final static void syncToFile(){
		Blacklist[] blacklists = (Blacklist[]) Blacklist.getBlacklists().values().toArray(new Blacklist[]{});
		String[] blacklistsAsString = new String[blacklists.length];
		
		if(blacklistsAsString.length == 0) {
			throw new AssertionError("No blacklists to save, this should be impossible!");
		}
		for(int i = 0; i < blacklistsAsString.length; i++) {
			blacklistsAsString[i] = blacklists[i].toNBT().toString();
		}
		
		if(Blacklist.getActiveBlacklist() == null) {
			throw new AssertionError(new NullPointerException("No active blacklist, this should be impossible!"));
		}
		
		GUIConfig.currentBlacklist.set(Blacklist.getActiveBlacklist().getName());
		GUIConfig.blacklists.setValues(blacklistsAsString);
		new ConfigChangedEvent(MODID, CONFIG.getConfigFile().getName(), true, false);
		getConfig().save();
	}
	
	public static final Configuration getConfig(){
		return CONFIG;
	}
	
	public static final KeyBinding[] getKeyBindings(){
		return KEYBINDINGS;
	}
	
	private final static void config(){
		GUIConfig.initConfigValues();
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
