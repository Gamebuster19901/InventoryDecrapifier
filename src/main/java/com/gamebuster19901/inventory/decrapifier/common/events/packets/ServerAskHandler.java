package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.management.ClientDecrapifier;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerAskHandler implements IMessageHandler<ServerAskPickupItemPacket, IMessage>{

	@Override
	public IMessage onMessage(ServerAskPickupItemPacket message, MessageContext ctx) {
		return ((ClientDecrapifier)Main.Proxy.getDecrapifier()).canPickup(message);
	}
}
