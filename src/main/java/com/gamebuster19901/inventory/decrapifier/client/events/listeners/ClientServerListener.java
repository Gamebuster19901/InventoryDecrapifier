package com.gamebuster19901.inventory.decrapifier.client.events.listeners;


import static net.minecraftforge.fml.relauncher.Side.CLIENT;

import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerHasModHandler;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientServerListener{
	@SubscribeEvent
	public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent e){
		ServerHasModHandler.setServerHasMod(false);
		ClientProxy.setConnectionType(e.getConnectionType());
	}
	
	@SubscribeEvent
	public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e){
		ServerHasModHandler.setServerHasMod(false);
		ClientProxy.setConnectionType("NONE");
	}
}
