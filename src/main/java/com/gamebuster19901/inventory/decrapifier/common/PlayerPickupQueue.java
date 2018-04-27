package com.gamebuster19901.inventory.decrapifier.common;

import java.util.ArrayDeque;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerPickupQueue extends ArrayDeque<EntityItem>{
	private final EntityPlayer player;
	public PlayerPickupQueue(EntityPlayer p){
		player = p;
	}
	
	public PlayerPickupQueue(Collection<? extends EntityItem> c, EntityPlayer p){
		super(c);
		player = p;
	}
	
	public EntityPlayer getPlayer(){
		return player;
	}
	
	@Override
	public boolean contains(Object o){
		for(EntityItem i : this){
			if(o instanceof Entity){
				if(((Entity)o).isEntityEqual(i)){
					return true;
				}
				return false;
			}
		}
		return false;
	}
}
