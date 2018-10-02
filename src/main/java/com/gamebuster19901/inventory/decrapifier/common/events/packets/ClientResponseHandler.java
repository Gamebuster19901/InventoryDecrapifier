package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import java.util.ArrayList;

import com.gamebuster19901.inventory.decrapifier.Main;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientResponseHandler implements IMessageHandler<ClientResponsePacket, IMessage>{

	@Override
	public IMessage onMessage(ClientResponsePacket message, MessageContext ctx) {
		ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
			for(Entity e : new ArrayList<Entity>(ctx.getServerHandler().player.getServerWorld().loadedEntityList)){
				if (message != null && e.getUniqueID().equals(message.getUUID())){
					if (e instanceof EntityItem){
						Main.Proxy.getServerDecrapifier().addPickup(ctx.getServerHandler().player,(EntityItem) e, message.canPickup());
					}
					else{
						ctx.getServerHandler().disconnect(new TextComponentString("[InventoryDecrapifier] Malformed packets:\n" + e + "\nis not an EntityItem"));
					}
				}
			}
		});
		
		return null;
	}
}