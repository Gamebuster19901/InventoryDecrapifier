package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import com.gamebuster19901.inventory.decrapifier.Main;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerHasModHandler implements IMessageHandler<ServerHasModPacket, IMessage>{
	public static boolean serverHasMod = false;
	
	@Override
	public IMessage onMessage(ServerHasModPacket message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			serverHasMod = true;
			Main.Proxy.NETWORK.sendToServer(new ClientHasModPacket());
		});
		return null;
	}
	
	public static boolean serverHasMod() {
		return serverHasMod;
	}

	public static void setServerHasMod(boolean hasMod) {
		serverHasMod = hasMod;
	}
}
