package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ServerAskPickupItemPacket implements IMessage{
	private UUID UUID;
	private ItemStack item;
	
	public ServerAskPickupItemPacket(){};
	
	public ServerAskPickupItemPacket(UUID UUID, ItemStack item){
		this.UUID = UUID;
		this.item = item;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		UUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		item = new ItemStack(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, UUID.toString());
		ByteBufUtils.writeTag(buf, item.serializeNBT());
	}
	
	public UUID getUUID(){
		return UUID;
	}
	
	public ItemStack getItem() {
		return item;
	}
}
