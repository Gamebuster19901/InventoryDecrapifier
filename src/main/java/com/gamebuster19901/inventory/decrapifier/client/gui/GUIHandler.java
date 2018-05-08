package com.gamebuster19901.inventory.decrapifier.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler{
	public static final int GUI_BLACKLIST = 0;
	public static final int GUI_BLACKLIST_ADD_ID = 1;
	public static final int GUI_BLACKLIST_ADD_ORE = 2;
	public static final int GUI_BLACKLIST_ADD_WILD = 3;
	public static final int GUI_BLACKLIST_ADD_BLACKLIST = 4;
	public static final int GUI_BLACKLIST_DEL_BLACKLIST_CONFIRM = 5;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Object gui;
		switch(ID){
			case GUI_BLACKLIST: gui = new GUIBlacklist();
				break;
			case GUI_BLACKLIST_ADD_ID: gui = new GUIAddToBlacklist(false);
				break;
			case GUI_BLACKLIST_ADD_ORE: gui = new GUIAddToBlacklist(true);
				break;
			case GUI_BLACKLIST_ADD_WILD: gui = new GUIAddToBlacklist();
				break;
			case GUI_BLACKLIST_ADD_BLACKLIST: gui = new GUIAddBlacklist();
				break;
			case GUI_BLACKLIST_DEL_BLACKLIST_CONFIRM: gui = new GUIDelBlacklist();
				break;
				
			default: gui = null;
		}
		return gui;
	}
}
