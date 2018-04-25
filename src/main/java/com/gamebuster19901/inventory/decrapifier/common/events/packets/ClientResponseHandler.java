package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import java.util.ArrayList;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.server.ServerDecrapifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientResponseHandler implements IMessageHandler<ClientResponsePacket, IMessage>{

	@Override
	public IMessage onMessage(ClientResponsePacket message, MessageContext ctx) {
		for(Entity e : new ArrayList<Entity>(ctx.getServerHandler().player.getServerWorld().loadedEntityList)){
			if (e.getUniqueID().equals(message.getUUID())){
				if (e instanceof EntityItem){
					if(message.canPickup()) {
						Main.Proxy.getServerDecrapifier().addPickup(ctx.getServerHandler().player,(EntityItem) e);
					}
				}
				else{
					ctx.getServerHandler().disconnect(new TextComponentString("[InventoryDecrapifier] Malformed packets:\n" + e + "\nis not an EntityItem"));
					//func_194028_b = disconnect
				}
			}
		}
		return null;
	}
}