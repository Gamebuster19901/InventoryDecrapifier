package com.gamebuster19901.inventory.decrapifier.common.events.pickup;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class PickupResponse {
	private EntityPlayer player;
	private EntityItem item;
	private boolean shouldPickup;
	
	public PickupResponse(EntityPlayer player, EntityItem item, boolean shouldPickup) {
		this.player = player;
		this.item = item;
		this.shouldPickup = shouldPickup;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public EntityItem getItem() {
		return item;
	}
	
	public boolean shouldPickup() {
		return shouldPickup;
	}
}
