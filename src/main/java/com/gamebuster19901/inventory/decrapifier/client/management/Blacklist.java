package com.gamebuster19901.inventory.decrapifier.client.management;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Blacklist {
	public static final Blacklist INSTANCE = new Blacklist();
	private final ListItemArrayList bannedIds = new ListItemArrayList();
	private final ListItemArrayList bannedOres = new ListItemArrayList();
	
	private Blacklist() {};
	
	public final boolean contains(ItemStack item){
		return bannedIds.contains(item) || bannedOres.contains(item);
	}
	
	public final boolean contains(ListItem listItem){
		return bannedIds.contains(listItem) || bannedOres.contains(listItem);
	}
	
	public final boolean addToBlacklist(ListItem i){
		if (i != null){
			if(i.isOre()) {
				return bannedOres.add(i);
			}
			
			return bannedIds.add(i);
		}
		return false;
	}
	
	public final boolean removeFromBlacklist(ListItem i){
		if(i != null) {
			if(i.isOre()) {
				return bannedOres.remove(i);
			}
			return bannedIds.remove(i);
		}
		return false;
	}
	
	public final boolean replaceFromBlacklist(ListItem old, ListItem new_){
		if(old.isOre() == new_.isOre()) {
			if(old.isOre()) {
				return bannedOres.replace(old, new_);
			}
			return bannedIds.replace(old, new_);
		}
		String type1 = old.isOre() ? "OreDictionary" : "Id";
		String type2 = new_.isOre() ? "OreDictionary" : "Id";
		String type3 = old.getDataAsString();
		String type4 = new_.getDataAsString();
		throw new AssertionError("Attempted to replace a " + type1 + " value with a " +  type2 + " value.\n\n" + type3 + "\n" + type4);
	}
	
	public final ListItemArrayList getBannedItems(){
		ListItemArrayList items = new ListItemArrayList();
		items.addAll(bannedOres);
		items.addAll(bannedIds);
		return items;
	}
	
	public final ListItemArrayList getBannedOres() {
		ListItemArrayList items = new ListItemArrayList();
		items.addAll(bannedOres);
		return items;
	}
	
	public final ListItemArrayList getBannedIds() {
		ListItemArrayList items = new ListItemArrayList();
		items.addAll(bannedIds);
		return items;
	}
	
	public final void clearBannedItems(){
		bannedOres.clear();
		bannedIds.clear();
	}
}
