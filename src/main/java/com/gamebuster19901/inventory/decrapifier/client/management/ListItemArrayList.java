package com.gamebuster19901.inventory.decrapifier.client.management;

import java.util.ArrayList;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraft.item.ItemStack;

public final class ListItemArrayList extends ArrayList<ListItem>{
	@Override
	public boolean contains(Object o){
		for(ListItem i : this){
			if (o instanceof ItemStack){
				if (i.contains(o)){
					return true;
				}
			}
			else if (o instanceof ListItem){
				if (i.equals(o)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean remove(Object o){
		boolean ret = super.remove(o);
		((ClientProxy) Main.Proxy).syncToFile();
		return ret;
	}
	
	@Override
	public boolean add(ListItem l){
		boolean ret = false;
		if (!this.contains(l)){
			ret = true;
			super.add(l);
		}
		((ClientProxy) Main.Proxy).syncToFile();
		return ret;
	}
	
	public String[] getStrings(){
		String[] ret = new String[this.size()];
		int i = 0;
		for(ListItem l : this){
			ret[i] = l.toString();
			i++;
		}
		return ret;
	}

	public boolean replace(ListItem old, ListItem new_) {
		int i = 0;
		for(ListItem l : this){
			if (l.equals(old)){
				set(i, new_);
				return true;
			}
			i++;
		}
		return false;
	}
}
