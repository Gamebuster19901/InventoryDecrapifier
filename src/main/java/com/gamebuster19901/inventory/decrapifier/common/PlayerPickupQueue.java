package com.gamebuster19901.inventory.decrapifier.common;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.gamebuster19901.inventory.decrapifier.common.events.pickup.PickupResponse;

import net.minecraft.entity.Entity;

public class PlayerPickupQueue extends ConcurrentLinkedDeque<PickupResponse>{
	
	
	@Override
	public boolean contains(Object o){
		for(PickupResponse r : this){
			if(o instanceof Entity){
				if(((Entity)o).isEntityEqual(r.getItem())){
					return true;
				}
				return false;
			}
		}
		return false;
	}
}
