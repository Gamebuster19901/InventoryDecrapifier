package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.Main.MODID;
import static com.gamebuster19901.inventory.decrapifier.Main.MODNAME;

import java.util.ArrayList;
import java.util.List;

import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GUIConfig extends net.minecraftforge.fml.client.config.GuiConfig{
	
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
}
