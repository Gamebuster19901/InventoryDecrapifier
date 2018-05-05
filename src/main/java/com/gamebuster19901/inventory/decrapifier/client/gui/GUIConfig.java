package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.Main.MODID;
import static com.gamebuster19901.inventory.decrapifier.Main.MODNAME;

import java.util.ArrayList;
import java.util.List;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GUIConfig extends net.minecraftforge.fml.client.config.GuiConfig{
	public static final Configuration CONFIG = ClientProxy.getConfig();
	public static final Property blacklistEnabled = CONFIG.get("Main", "isBlackListOn", false, "If true, you will not pickup anything in the blacklist, unless you press the 'Pick up Item' key");
	public static final Property highlightEnabled = CONFIG.get("Main", "highlightSelectedItem", true, "If true, the item you are selecting for pickup will glow");
	public static final Property pickupByDefault = CONFIG.get("Main", "pickupItemsByDefault", true, "If false, you will not pick up ANYTHING unless you press the 'Pick up Item' key while looking at the item you want");
	public static final Property currentBlacklist = CONFIG.get("Blacklists", "currentBlacklist", "Blacklist 1", "The name of the current blacklist");
	public static final Property blacklists = CONFIG.get("Blacklists", "blacklists", new String[]{}, "All of your blacklists represented as NBT. Do not touch.");
	
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
      
        return list;
    }
	
    private static IConfigElement categoryElement(String category, String name, String tooltip_key) {
        return new DummyConfigElement.DummyCategoryElement(name, tooltip_key, new ConfigElement(ClientProxy.getConfig().getCategory(category)).getChildElements());
    }
    
    public static void initConfigValues() {
    	//config values initialized via classloading
    }
}
