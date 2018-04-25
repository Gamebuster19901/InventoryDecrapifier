package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerHasModHandler implements IMessageHandler<ServerHasModPacket, IMessage>{
	public static boolean serverHasMod = false;
	
	@Override
	public IMessage onMessage(ServerHasModPacket message, MessageContext ctx) {
		serverHasMod = true;
		return new ClientHasModPacket();
	}
	
	public static boolean serverHasMod() {
		return serverHasMod;
	}

	public static void setServerHasMod(boolean hasMod) {
		serverHasMod = hasMod;
	}
}
