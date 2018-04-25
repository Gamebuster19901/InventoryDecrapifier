package com.gamebuster19901.inventory.decrapifier.server;

import java.util.LinkedHashSet;

import com.gamebuster19901.inventory.decrapifier.common.PlayerPickupQueue;

import net.minecraft.entity.player.EntityPlayer;

public class AllPlayerPickupsHashSet extends LinkedHashSet<PlayerPickupQueue>{
	public PlayerPickupQueue get(EntityPlayer p){
		for(PlayerPickupQueue set : this){
			if (set.getPlayer() == p){
				return set;
			}
		}
		return null;
	}
}
