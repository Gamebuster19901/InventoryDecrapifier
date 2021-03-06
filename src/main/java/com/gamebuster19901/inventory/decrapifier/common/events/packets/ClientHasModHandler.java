package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import com.gamebuster19901.inventory.decrapifier.Main;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientHasModHandler implements IMessageHandler<ClientHasModPacket, IMessage>{
	@Override
	public IMessage onMessage(ClientHasModPacket message, MessageContext ctx) {
		ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
			Main.Proxy.getServerDecrapifier().getClient(ctx.getServerHandler().player).setHasMod(true);
		});
		return null;
	}
}
