package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.Main.MODID;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.WEST;

import org.lwjgl.opengl.GL11;

import com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public abstract class EditScreen extends GuiScreen{
	public EditScreen(){
		super();
	}
	
	protected abstract class ImageButton extends GuiButton{
		private ResourceLocation image;
		protected int imageWidth;
		protected int imageHeight;
		protected int mode;
		public ImageButton(int buttonId, int x, int y, ResourceLocation image){
			super(buttonId,x,y,16,16,"");
			this.image = image;
		}
		
		public ImageButton(int buttonId, int x, int y, int width, int height, ResourceLocation image){
			super(buttonId, x, y, width, height, "");
			this.image = image;
		}
		
		protected abstract void onClick();
		
		@Override
		public final void drawButton(Minecraft mc, int mouseX, int mouseY, float unknown){ //drawButton()
			if (this.visible){
				GL11.glDisable(GL11.GL_LIGHTING);
				mc.getTextureManager().bindTexture(image);										  //text w and h //display w and height
				EditScreen.this.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, imageWidth, imageHeight);
			}
		}
	}
	
	protected abstract class AddButton extends ImageButton implements Dependant{
		public AddButton(int buttonId, int x, int y){
			super(buttonId, x, y, new ResourceLocation(MODID, "textures/gui/add.png"));
			imageWidth = 16;
			imageHeight = 16;
		}
	}
	
	protected abstract class DeleteButton extends ImageButton{
		public DeleteButton(int buttonId, int x, int y){
			super(buttonId, x, y, new ResourceLocation(MODID, "textures/gui/delete.png"));
			imageWidth = 16;
			imageHeight = 16;
		}
	}
	
	protected abstract class EditButton extends ImageButton{
		public EditButton(int buttonId, int x, int y){
			super(buttonId, x, y, new ResourceLocation(MODID, "textures/gui/edit.png"));
			imageWidth = 16;
			imageHeight = 16;
		}
	}
	
	protected abstract class ArrowButton extends ImageButton implements Dependant{
		private EnumFacing direction;
		public ArrowButton(int id, int x, int y, EnumFacing direction){
			super(id, x, y, 
					direction == EAST || direction == WEST ? 16: 32, //if button is left or right, make it skinny, else make it wide
					direction == EAST || direction == WEST ? 32: 16, //if button is left or right, make it tall, else make it short
					null);
			this.direction = direction;
			setImage();
			if (super.image == null){
				throw new AssertionError("Image should not be null here!");
			}
		}
		
		private void setImage(){
			switch(direction){
				case NORTH:
					super.image = new ResourceLocation(MODID, "textures/gui/up.png");
						imageHeight = 16;
						imageWidth = 32;
					break;
				case EAST: super.image = new ResourceLocation(MODID, "textures/gui/right.png");
						imageHeight = 32;
						imageWidth = 16;
					break;
				case SOUTH: super.image = new ResourceLocation(MODID, "textures/gui/down.png");
						imageHeight = 16;
						imageWidth = 32;
					break;
				case WEST: super.image = new ResourceLocation(MODID, "textures/gui/left.png");
						imageHeight = 32;
						imageWidth = 16;
					break;
				default:
					throw new AssertionError("Direction is not valid!");
			}
		}
	}
	
	protected interface Dependant{
		GUISegment getGUISegment();
	}
}
