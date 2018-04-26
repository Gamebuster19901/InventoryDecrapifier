package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.Main.MODID;
import static com.gamebuster19901.inventory.decrapifier.Main.MODNAME;

import java.util.ArrayList;
import java.util.List;

import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GUIConfig extends net.minecraftforge.fml.client.config.GuiConfig{
	public static final Configuration CONFIG = ClientProxy.getConfig();
	public static final Property blacklistIds = CONFIG.get("Main", "blackListedItemsByID", new String[]{"NULL"}, "A list of items (by ID) that you will not automatically pick up.\nThis only takes effect if the blacklist is on.");
	public static final Property blacklistOres = CONFIG.get("Main", "blackListedItemsByOreDictionary", new String[]{"NULL"}, "A list of Ore Dictionary names. If an item corresponds with the Ore Dictionary name, you will not pick up the item. \n Only takes effect if the blacklist is on");
	public static final Property blacklistEnabled = CONFIG.get("Main", "isBlackListOn", false, "If true, you will not pickup anything in the blacklist, unless you press the 'Pick up Item' key");
	public static final Property highlightEnabled = CONFIG.get("Main", "highlightSelectedItem", true, "If true, the item you are selecting for pickup will glow");
	public static final Property pickupByDefault = CONFIG.get("Main", "pickupItemsByDefault", true, "If false, you will not pick up ANYTHING unless you press the 'Pick up Item' key while looking at the item you want");
	public static final Property tossOnPickup = CONFIG.get("NonModServer", "tossBlackListedItemsOnPickup", true, "If the blacklist is on and this is true, then you will toss blacklisted items on pickup if you're not on a server with this mod installed " + TextFormatting.RED + " [NOT YET IMPLEMENTED]");
	
	public GUIConfig(GuiScreen parent){
		super(
				parent,
				getConfigElements(), 
				MODID,
				false,
				false,
				MODNAME
		);
	}
	
	private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
      
        //Add categories to config GUI
        list.add(categoryElement("Main", "General", "config.invDeCrap.category.general"));
        list.add(categoryElement("NonModServer", "When server doesn't have this mod installed", "config.invDeCrap.category.NOS"));
      
        return list;
    }
	
    private static IConfigElement categoryElement(String category, String name, String tooltip_key) {
        return new DummyConfigElement.DummyCategoryElement(name, tooltip_key, new ConfigElement(ClientProxy.getConfig().getCategory(category)).getChildElements());
    }
    
    public static void initConfigValues() {
    	//config values initialized via classloading
    }
}
