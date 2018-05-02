package com.gamebuster19901.inventory.decrapifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION, guiFactory = "com.gamebuster19901.inventory.decrapifier.client.gui.GUIFactory", canBeDeactivated=true, acceptableRemoteVersions = "*")
public class Main{
	public static final String MODID = "invdecrap";
	public static final String MODNAME = "Inventory Decrapifier";
	public static final String VERSION = "0.12.1.4 - 1.12.2";
	public static final Logger LOGGER = LogManager.getLogger(MODNAME);
	private static Main instance;
	@SidedProxy(serverSide = "com.gamebuster19901.inventory.decrapifier.proxy.ServerProxy", clientSide = "com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy")
	public static com.gamebuster19901.inventory.decrapifier.proxy.Proxy Proxy;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e){
		instance = this;
		Proxy.preInit(e);
	}
	
	
	@EventHandler
	public void init(FMLInitializationEvent e){
		Proxy.init(e);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e){
		Proxy.postInit(e);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent e) {
		Proxy.serverStarting(e);
	}
	
	public static Main getInstance(){
		return instance;
	}
}
