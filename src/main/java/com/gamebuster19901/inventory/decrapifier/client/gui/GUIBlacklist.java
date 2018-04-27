package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.Mode.Delete;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.Mode.Edit;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler.GUI_BLACKLIST_ADD_ID;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler.GUI_BLACKLIST_ADD_ORE;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler.GUI_BLACKLIST_ADD_WILD;

import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment.Top;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment.Bottom;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment.Sidebar;

import static net.minecraft.util.EnumFacing.EAST; //right
import static net.minecraft.util.EnumFacing.WEST; //left

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.gui.components.Overlay;
import com.gamebuster19901.inventory.decrapifier.client.management.Blacklist;
import com.gamebuster19901.inventory.decrapifier.client.management.ListItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;

public class GUIBlacklist extends EditScreen{ //Editable extends GuiScreen
	static final int xPadding = 85; //minimum distance from vertical sides of screen
	static final int yPadding = 25; //minimum distance from horizontal sides of screen
	private final Overlay overlay = new Overlay(this);
	private Mode mode = Edit;
	private boolean hasRegistered = false; //A flag to tell if the screen needs to be updated, false means it needs to update
	private ArrayList<GuiButton> components = new ArrayList<GuiButton>(); //list of GuiButtons, used to update GuiScreen.buttonList
	private float prevVignetteBrightness = 1f;
	
	private static int topPage = 0;
	private static int bottomPage = 0;
	private static int LeftPage = 0;
	
	
	public GUIBlacklist(){
		super();
	}
	
	@Override
	public void initGui(){
		hasRegistered = false;
		buttonList.clear();
		Keyboard.enableRepeatEvents(true);
	}
	
	
	@Override
	public void updateScreen(){
		if (!hasRegistered){
			constructComponents();
			buttonList.clear();
			for(GuiButton component : components){
				if (component != null){
					//check if component is out of bounds, if it is, don't make it visible
					if (component instanceof ItemButton && (component.x < this.xPadding || component.x + component.width > width - xPadding)){ //5 had to be added to fix the selection arrow moving over but the last column still remaining visible, unknown as to why
						component.visible = false;
					}
					else{
						component.visible = true;
					}
					buttonList.add(component);
				}
			}
			hasRegistered = true;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		String message;
		GL11.glScalef(2, 2, 2);
		if(mode == Delete){
			message = I18n.format("blacklist.deletewarning.top");
			fontRenderer.drawString(message, (width / 4) - (fontRenderer.getStringWidth(message) / 2), 2, 16733525);
			message = I18n.format("blacklist.deletewarning.bottom");
			fontRenderer.drawString(message, (width / 4) - (fontRenderer.getStringWidth(message) / 2), (this.height / 4) - (fontRenderer.FONT_HEIGHT / 2), 16733525);
		}
		else{
			message = I18n.format("blacklist.category.id");
			fontRenderer.drawString(TextFormatting.UNDERLINE + message, (width / 4) - (fontRenderer.getStringWidth(message) / 2), 2, 16777215);
			message = I18n.format("blacklist.category.ore");
			fontRenderer.drawString(TextFormatting.UNDERLINE + message, (width / 4) - (fontRenderer.getStringWidth(message) / 2), (this.height / 4) - (fontRenderer.FONT_HEIGHT / 2), 16777215);
		}
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		GlStateManager.color(1f, 1f, 1f);
		/*
		 * Make arrows visible if the item buttons leave the screen
		 */
		for(GuiButton s : components){
			if (s instanceof ArrowButton){
				ArrowButton curArrow = (ArrowButton) s;
				curArrow.updatePosition();
				if (curArrow.seg == Top){
					if(curArrow.direction == WEST){
						if (topPage <= 0){
							curArrow.visible = false;
						}
					}
					else if(curArrow.direction == EAST){
						if (topPage >= getTotalColumns(Top) - getVisibleColumnCount(Top)){
							curArrow.visible = false;
						}
					}
				}
				else if (curArrow.seg == Bottom){
					if (curArrow.direction == WEST){
						if (bottomPage <=0){
							curArrow.visible = false;
						}
					}
					else if (curArrow.direction == EAST){
						if (bottomPage >= getTotalColumns(Bottom) - getVisibleColumnCount(Bottom)){
							curArrow.visible = false;
						}
					}
				}
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	private ItemButton getLastItemButton(GUISegment s, int start){
		for(int i = start; i > 0; i--){
			if(components.get(i) instanceof ItemButton){
				ItemButton lastButton = (ItemButton) components.get(i);
				if (lastButton.segment == s){
					return lastButton;
				}
			}
		}
		return null;
	}
	
	private ItemButton getFirstItemButton(GUISegment s){
		for(GuiButton b : components){
			if (b instanceof ItemButton){
				ItemButton button = (ItemButton) b;
				if (button.segment == s){
					return button;
				}
			}
		}
		return null;
	}
	
	/**
	 * creates and updates buttons on the screen, used to keep clutter out of the updateScreen method.
	 */
	private void constructComponents(){
		boolean flag = false;
		int id = 0;
		int total = 0;
		int col = 0; //x
		int row = 0; //y
		clear();
		for(ListItem l : Blacklist.INSTANCE.getBannedIds()){
			if (l != null){
				if(!l.isOre()){
					if (total != 0 && total % 3 == 0){
						row = 0;
						col++;
					}
					int realcol = col - topPage;
					components.add(new ItemButton(id++, xPadding + (((realcol * 16) + (realcol * ItemButton.xPadding))), yPadding + (row * 16) + (row * ItemButton.yPadding), l));
					row++;
					total++;
				}
				else {
					throw new AssertionError("OreDictionary value exists in ID only list! (" + l.getDataAsString() + ")");
				}
			}
		}
		for(ListItem l : Blacklist.INSTANCE.getBannedOres()) {
			if(l != null) {
				if(l.isOre()) {
					if (!flag){
						total = 0;
						col = 0;
						row = 0;
						flag = !flag;
					}
					if(total != 0 && total % 3 == 0){
						row = 0;
						col++;
					}
					int realcol = col - bottomPage;
					components.add(new ItemButton(id++, xPadding + (((realcol * 16) + (realcol * ItemButton.xPadding))), (height/2 + yPadding) + (row * 16) + (row * ItemButton.yPadding),l));
					row++;
					total++;
				}
			}
			else{
				throw new IllegalStateException("List items cannot be null!");
			}
		}
		addCustomButton(new EditButton(id++, width / 2 - 24, height / 2 - 26));
		addCustomButton(new AddButton(id, components.get(id - 1).x + components.get(id - 1).width, components.get(id - 1).y, Top));
		id++;
		addCustomButton(new DeleteButton(id, components.get(id - 1).x + components.get(id - 1).width, components.get(id - 1).y));
		id++;
		addCustomButton(new ArrowButton(id++, 0,0, EAST, Top));
		addCustomButton(new ArrowButton(id++, 0,0, WEST, Top));
		addCustomButton(new EditButton(id++, width / 2 - 24, height - 39));
		addCustomButton(new AddButton(id, components.get(id - 1).x + components.get(id - 1).width, components.get(id - 1).y, Bottom));
		id++;
		addCustomButton(new DeleteButton(id, components.get(id - 1).x + components.get(id - 1).width, components.get(id - 1).y));
		id++;
	}
	
	private void clear(){
		components.clear();
		buttonList.clear();
	}
	
	private GuiButton addCustomButton(GuiButton button){
		components.add(button);
		return button;
	}
	
	/**
	 * Called when a button is pressed down
	 * @param button the button that was pressed
	 */
	@Override
	protected void actionPerformed(GuiButton button){
		if (button instanceof ItemButton){
			if (mode == Delete && Blacklist.INSTANCE.removeFromBlacklist(((ItemButton) button).getListItem())){
				buttonList.remove(button);
				button = null;
				initGui();
			}
			else if (mode == Edit){
				GUIAddToBlacklist.passedData = ((ItemButton) button).getListItem();
				mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_WILD, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
			return;
		}
		if (button instanceof ImageButton){
			((ImageButton) button).onClick();
		}
	}
	/**
	 * Gets the amount of visible columns
	 * @param isOre
	 * @return
	 */
	private int getVisibleColumnCount(GUISegment s){
		int ret = 0;
		if(s == Top){
			for(GuiButton b : components){
				if (b instanceof ItemButton && ((ItemButton)b).segment == Top && b.visible){
					ret++;
				}
			}
			return (int)Math.ceil((double)ret / 3d);
		}
		throw new AssertionError();
	}
	
	private int getTotalColumns(GUISegment s){
		int ret = 0;
		if(s == Top){
			for(GuiButton b : components){
				if (b instanceof ItemButton && ((ItemButton)b).segment == Top){
					ret++;
				}
			}
			return (int)Math.ceil((double)ret / 3d);
		}
		throw new AssertionError();
	}
	
	private final class ItemButton extends GuiButton{
		static final int xPadding = 5;
		static final int yPadding = 5;
		
		private ListItem item;
		private final GUISegment segment;
		
		public ItemButton(int buttonId, int x, int y, ListItem l) {
			super(buttonId, x, y, 16, 16, "");
			item = l;
			if (item.isOre()){
				segment = Bottom;
			}
			else{
				segment = Top;
			}
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float unknown){
			assert item != null;
			if (this.visible){
				//super.drawButton(mc, mouseX, mouseY);
				GL11.glDisable(GL11.GL_LIGHTING);
				drawItem();
			}
		}
		
		private ListItem getListItem(){
			return item;
		}
		
		private void drawItem(){
			itemRender.renderItemAndEffectIntoGUI(item.getPolyStack(), x, y);
			itemRender.renderItemOverlayIntoGUI(fontRenderer, item.getPolyStack(), x, y, item.getDataAsString());
		}
		
		public ItemStack getItemStack(){
			return new ItemStack(item.getItem(), 1, item.getData()[0]);
		}
	}
	
	private final class AddButton extends EditScreen.AddButton{
		private final GUISegment seg;
		public AddButton(int buttonId, int x, int y, GUISegment s){
			super(buttonId, x, y);
			seg = s;
		}
		
		@Override
		protected void onClick() {
			//mc.player.closeScreen();
			if(seg == Top){
				mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_ID, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
			else if (seg == Bottom){
				mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_ORE, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
		}

		@Override
		public GUISegment getGUISegment() {
			// TODO Auto-generated method stub
			return seg;
		}
	}
	
	private final class DeleteButton extends EditScreen.DeleteButton{
		public DeleteButton(int buttonId, int x, int y) {
			super(buttonId, x, y);
		}

		@Override
		protected void onClick() {
			if (GUIBlacklist.this.mode != Delete){
				GUIBlacklist.this.mode = Delete;
			}
		}
	}
	
	private final class EditButton extends EditScreen.EditButton{
		public EditButton(int buttonId, int x, int y){
			super(buttonId, x, y);
		}
		
		protected void onClick(){
			if (GUIBlacklist.this.mode != Edit){
				GUIBlacklist.this.mode = Edit;
			}
		}
	}
	
	private final class ArrowButton extends EditScreen.ArrowButton{
		private final EnumFacing direction;
		private final GUISegment seg;
		public ArrowButton(int buttonId, int x, int y, EnumFacing d, GUISegment s){
			super(buttonId, -100, -100, d);
			seg = s;
			direction = d;
		}
		
		public void onClick(){
			switch (seg){
				case Top: switch (direction){
					case EAST: topPage++; break;
					case WEST: topPage--; break;
					default: throw new IllegalStateException();
				} break;
				default: throw new AssertionError();
			}
			GUIBlacklist.this.initGui();
		}

		@Override
		public GUISegment getGUISegment() {
			return seg;
		}
		
		public void updatePosition(){
			if (seg == Sidebar){
				this.x -= this.width;
			}
			else{
				if (this.direction == EAST){
					x = (getVisibleColumnCount(Top) * 16 + (getVisibleColumnCount(Top) * ItemButton.xPadding)) + xPadding;
				}
				else{
					x = xPadding - width - ItemButton.xPadding;
				}
				y = (2 * 16 + (3 * ItemButton.yPadding) + yPadding);
				this.y -= this.height;
			}
		}
	}
	
	static enum Mode{
		Edit,
		Delete;
	}
	
	static enum GUISegment{
		Unknown((byte)0),
		Top((byte)1),
		Bottom((byte)2),
		Sidebar((byte)3);
		
		final byte value;
		GUISegment(byte val){
			value = val;
		}
		
		public byte toInt(){
			if (value >= 0 || value <= 3){
				return value;
			}
			return 0;
		}
	}
}
