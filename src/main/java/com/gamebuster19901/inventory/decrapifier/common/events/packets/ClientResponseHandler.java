package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

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
		for(Entity e : new ArrayList<Entity>(ctx.getServerHandler().player.getServerWorld().loadedEntityList)){
			if (e.getUniqueID().equals(message.getUUID())){
				if (e instanceof EntityItem){
					if(ctx.getServerHandler().player.getUniqueID().equals(message.getPlayer())){
						Main.Proxy.getServerDecrapifier().addPickup(ctx.getServerHandler().player,(EntityItem) e, message.canPickup());
					}
					else {
						Main.LOGGER.log(Level.ERROR, "MessageContext in invalid state, attempted to process packet for " + ctx.getServerHandler().player.getName() + " that was meant for " + ctx.getServerHandler().player.world.getPlayerEntityByUUID(message.getPlayer()).getName() + "... Ignoring the packet!");
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