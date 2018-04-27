package com.gamebuster19901.inventory.decrapifier.client.gui.components;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class Overlay extends ArrayList<Overlay.Image>{
	private GuiScreen parent;
	public Overlay(GuiScreen parent){
		this.parent = parent;
	}
	
	public void render(){
		for(Image i : this){
			parent.mc.getTextureManager().bindTexture(i.image);	
			parent.drawModalRectWithCustomSizedTexture(i.xPosition, i.yPosition, 0f, 0f, i.width, i.height, i.width, i.height);
		}
	}
	
	public static final class Image{
		private ResourceLocation image;
		public int xPosition;
		public int yPosition;
		public int width;
		public int height;
		
		public Image(ResourceLocation image, int x, int y, int w, int h){
			this.image = image;
			xPosition = x;
			yPosition = y;
			width = w;
			height = h;
		}
	}
}
