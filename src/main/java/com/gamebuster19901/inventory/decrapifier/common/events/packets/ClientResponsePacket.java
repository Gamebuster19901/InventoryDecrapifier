package com.gamebuster19901.inventory.decrapifier.common.events.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ClientResponsePacket implements IMessage{
	private UUID UUID;
	private boolean canPickup;
	
	public ClientResponsePacket(){};
	
	public ClientResponsePacket(ServerAskPickupItemPacket pkt, boolean canPickup) {
		this(pkt.getUUID(), canPickup);
	}
	
	private ClientResponsePacket(UUID UUID, boolean canPickup){
		this.UUID = UUID;
		this.canPickup = canPickup;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		UUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		canPickup = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, UUID.toString());
		buf.writeBoolean(canPickup);
	}
	
	public UUID getUUID(){
		return UUID;
	}
	
	public boolean canPickup() {
		return canPickup;
	}
}