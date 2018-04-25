package com.gamebuster19901.inventory.decrapifier.proxy;

import com.gamebuster19901.inventory.decrapifier.server.ServerDecrapifier;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ServerProxy extends Proxy{
	
	@Override
	public void preInit(FMLPreInitializationEvent e){
		SERVER_DECRAP = new ServerDecrapifier();
		DECRAP = SERVER_DECRAP;
		super.preInit(e);
	}
	
	@Override
	public void init(FMLInitializationEvent e){
		super.init(e);
	}
	
	@Override 
	public void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}
	
	@Override
	public void serverStarting(FMLServerStartingEvent e) {
		super.serverStarting(e);
	}
}
