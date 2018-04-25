package com.gamebuster19901.inventory.decrapifier.proxy;

import static com.gamebuster19901.inventory.decrapifier.Main.MODID;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler;
import com.gamebuster19901.inventory.decrapifier.client.management.ClientDecrapifier;
import com.gamebuster19901.inventory.decrapifier.common.CommonDecrapifier;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ClientHasModHandler;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ClientHasModPacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ClientResponseHandler;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ClientResponsePacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerAskHandler;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerAskPickupItemPacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerHasModHandler;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerHasModPacket;
import com.gamebuster19901.inventory.decrapifier.server.ServerDecrapifier;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Proxy{
	protected static CommonDecrapifier DECRAP;
	protected static ServerDecrapifier SERVER_DECRAP;
	public static SimpleNetworkWrapper NETWORK;
	
	public void preInit(FMLPreInitializationEvent e){
		NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		NETWORK.registerMessage(ServerAskHandler.class, ServerAskPickupItemPacket.class, 0, Side.CLIENT);
		NETWORK.registerMessage(ClientResponseHandler.class, ClientResponsePacket.class, 1, Side.SERVER);
		NETWORK.registerMessage(ClientHasModHandler.class, ClientHasModPacket.class, 2, Side.SERVER);
		NETWORK.registerMessage(ServerHasModHandler.class, ServerHasModPacket.class, 3, Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(DECRAP);
		MinecraftForge.EVENT_BUS.register(SERVER_DECRAP);
	}
	
	public void init(FMLInitializationEvent e){
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.getInstance(), new GUIHandler());
	}
	
	public void postInit(FMLPostInitializationEvent e){
		
	}
	
	public void serverStarting(FMLServerStartingEvent e) {
		
	}
	
	public static final CommonDecrapifier getDecrapifier(){
		return DECRAP;
	}
	
	public static final ServerDecrapifier getServerDecrapifier() {
		return SERVER_DECRAP;
	}
}
