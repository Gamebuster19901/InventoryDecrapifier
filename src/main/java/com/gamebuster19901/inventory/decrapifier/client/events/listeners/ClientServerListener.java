package com.gamebuster19901.inventory.decrapifier.client.events.listeners;


import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerHasModHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientServerListener{
	@SubscribeEvent
	public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e){
		ServerHasModHandler.setServerHasMod(false);
	}
}
