package com.gamebuster19901.inventory.decrapifier.client.management;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.gui.GUIConfig;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class Blacklist {
	
	private static LinkedHashMap<String, Blacklist> blacklists = new LinkedHashMap<String, Blacklist>();
	private static Blacklist activeBlacklist = null;
	
	private final ListItemArrayList bannedIds = new ListItemArrayList();
	private final ListItemArrayList bannedOres = new ListItemArrayList();
	private String name = "Blacklist " + blacklists.size() + 1;
	
	private Blacklist(boolean put) {
		blacklists.put(this.name, this);
	};
	
	public Blacklist(String name) {
		this.name = name;
		blacklists.put(this.name, this);
	}
	
	public final boolean contains(ItemStack item){
		return bannedIds.contains(item) || bannedOres.contains(item);
	}
	
	public final boolean contains(ListItem listItem){
		return bannedIds.contains(listItem) || bannedOres.contains(listItem);
	}
	
	public final boolean addToBlacklist(ListItem i, boolean sync){
		if (i != null){
			if(i.isOre()) {
				return bannedOres.add(i, sync);
			}
			
			return bannedIds.add(i, sync);
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
	
	public final String getName() {
		return name;
	}
	
	public final void clearBannedItems(){
		bannedOres.clear();
		bannedIds.clear();
	}
	
	public NBTTagCompound toNBT(){
		ListItemArrayList bannedIdsArrayList = getBannedIds();
		ListItemArrayList bannedOresArrayList = getBannedOres();
		
		NBTTagCompound ret = new NBTTagCompound();
		NBTTagList bannedIds = new NBTTagList();
		NBTTagList bannedOres = new NBTTagList();
		
		
		for(int i = 0; i < getBannedIds().size(); i++) {
			NBTTagString id = new NBTTagString(bannedIdsArrayList.get(i).toString());
			bannedIds.appendTag(id);
		}
		
		for(int i = 0; i < getBannedOres().size(); i++) {
			NBTTagString ore = new NBTTagString(bannedOresArrayList.get(i).toString());
			bannedOres.appendTag(ore);
		}
		
		ret.setString("name", name);
		ret.setTag("ids", bannedIds);
		ret.setTag("ores", bannedOres);
		
		return ret;
	}
	
	public void fromNBT(NBTTagCompound nbt) {
		this.name = nbt.getString("name");
		for(NBTBase base : nbt.getTagList("ids", 8)) {
			this.addToBlacklist(ListItem.fromString(((NBTTagString)base).getString(), false), false);
		}
		for(NBTBase base : nbt.getTagList("ores", 8)) {
			this.addToBlacklist(ListItem.fromString(((NBTTagString)base).getString(), true), false);
		}
	}
	
	public static final void clearBlacklists() {
		blacklists.clear();
		activeBlacklist = null;
	}
	
	public static final Blacklist getActiveBlacklist() {
		if(activeBlacklist == null) {
			LinkedHashMap<String, Blacklist> blacklists = getBlacklistsFromConfig();
			if(blacklists.size() == 0) {
				Blacklist b = new Blacklist(true);
				blacklists.put(b.name, b);
				if(blacklists.get(b.name) == null) {
					throw new AssertionError(new NullPointerException());
				}
			}
		}
		return activeBlacklist;
	}
	
	public static final boolean nameTaken(String name) {
		return blacklists.containsKey(name);
	}
	
	public static LinkedHashMap<String, Blacklist> getBlacklists(){
		return blacklists;
	}
	
	public static LinkedHashMap<String, Blacklist> getBlacklistsFromConfig(){
		LinkedHashMap<String, Blacklist> ret = new LinkedHashMap<String, Blacklist>();
		
		for(String s : GUIConfig.blacklists.getStringList()) {
			try {
				Blacklist b = new Blacklist(false);
				b.fromNBT(JsonToNBT.getTagFromJson(s));
				ret.put(b.name, b);
			} catch (NBTException e) {
				Main.LOGGER.log(Level.ERROR, "Invalid blacklist nbt, it may be ignored or deleted:");
				Main.LOGGER.catching(Level.ERROR, e);
				Main.LOGGER.log(Level.ERROR, "No further information is availible :/");
			}
		}
		return ret;
	}
	
	public static void updateBlacklistsFromConfig() {
		clearBlacklists();
		blacklists = getBlacklistsFromConfig();
		if(blacklists.size() == 0) {
			Main.LOGGER.log(Level.INFO, "No blacklists detected in config, creating a new one");
			Blacklist b = new Blacklist(true);
			activeBlacklist = blacklists.get(b.name);
		}
		else {
			activeBlacklist = blacklists.get(GUIConfig.currentBlacklist.getString());
		
			if(activeBlacklist == null) {
				Main.LOGGER.log(Level.ERROR, "The blacklist named (" + GUIConfig.currentBlacklist.getString() + ") doesn't exist, creating a blank one with that name. This shouldn't happen unless you directly altered the config file. If it did, let me know!");
				activeBlacklist = new Blacklist(GUIConfig.currentBlacklist.getString());
				((ClientProxy)Main.Proxy).syncToFile();
			}

		}
	}
}
