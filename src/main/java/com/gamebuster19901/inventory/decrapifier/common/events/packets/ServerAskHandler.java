package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import java.util.ArrayList;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.management.ClientDecrapifier;
import com.gamebuster19901.inventory.decrapifier.server.ServerDecrapifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerAskHandler implements IMessageHandler<ServerAskPickupItemPacket, IMessage>{

	@Override
	public IMessage onMessage(ServerAskPickupItemPacket message, MessageContext ctx) {
		return ((ClientDecrapifier)Main.Proxy.getDecrapifier()).canPickup(message);
	}
}
