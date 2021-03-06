package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.Mode.Delete;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.Mode.Edit;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler.*;

import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment.Top;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment.Bottom;
import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIBlacklist.GUISegment.Sidebar;

import static net.minecraft.util.EnumFacing.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.management.Blacklist;
import com.gamebuster19901.inventory.decrapifier.client.management.ListItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;

public class GUIBlacklist extends EditScreen implements GuiYesNoCallback{
	static final int xPadding = 105; //minimum distance from vertical sides of screen
	static final int yPadding = 25; //minimum distance from horizontal sides of screen
	private Mode mode = Edit;
	private float prevVignetteBrightness = 1f;
	
	private static int topPage = 0;
	private static int bottomPage = 0;
	private static int sidePage = 0;
	
	
	public GUIBlacklist(){
		super();
	}
	
	@Override
	public void initGui(){
		Keyboard.enableRepeatEvents(true);
		createComponents();
	}
	
	
	@Override
	public void updateScreen(){
		/*
		 * Make arrows visible if the item buttons leave the screen
		 */
		for(GuiButton s : buttonList){
			if (s instanceof ArrowButton){
				ArrowButton curArrow = (ArrowButton) s;
				curArrow.updatePosition();
				if (curArrow.seg == Top){
					if(curArrow.direction == WEST){
						curArrow.visible = topPage > 0;
					}
					else if(curArrow.direction == EAST){
						curArrow.visible = topPage < getTotalCount(Top) - getVisibleCount(Top);
					}
				}
				else if (curArrow.seg == Bottom){
					if (curArrow.direction == WEST){
						curArrow.visible = bottomPage > 0;
					}
					else if (curArrow.direction == EAST){
						curArrow.visible = bottomPage < getTotalCount(Bottom) - getVisibleCount(Bottom);
					}
				}
				else if (curArrow.seg == Sidebar) {
					if(curArrow.direction == NORTH) {
						curArrow.visible = sidePage > 0;
					}
					else if (curArrow.direction == SOUTH) {
						curArrow.visible = sidePage < getTotalCount(Sidebar) - getVisibleCount(Sidebar);
					}
				}
				continue;
			}
			else if (s instanceof ItemButton) {
				ItemButton i = (ItemButton) s;
				int realcol;
				int realrow;
				switch(i.segment) {
					case Top:
						realcol = i.column - topPage;
						realrow = yPadding + (i.row * 16) + (i.row * ItemButton.yPadding);
						break;
					case Bottom:
						realcol = i.column - bottomPage;
						realrow = (height/2 + yPadding) + (i.row * 16) + (i.row * ItemButton.yPadding);
						break;
					default:
						throw new AssertionError();
				}
				s.x = xPadding + (((realcol * 16) + (realcol * ItemButton.xPadding)));
				s.y = realrow;
				
				//check if item button is out of bounds, if it is, don't make it visible
				s.visible = !(s.x < this.xPadding || s.x + s.width > width - xPadding);
			}
			else if (s instanceof BlacklistButton) {
				((BlacklistButton)s).updatePosition();
				
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		String message;
		GlStateManager.pushMatrix();
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
		GL11.glScalef(1.3f, 1.3f, 1.3f);
		message = I18n.format("blacklist.category.blacklists");
		fontRenderer.drawString(TextFormatting.UNDERLINE + message, (xPadding / 2 - (int)(fontRenderer.getStringWidth(message) * (1f / 1.3f) - 4) - 10), 2, 16777215);
		GL11.glScalef(1 / 1.3f, 1 / 1.3f, 1 / 1.3f);
		GlStateManager.popMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	private ItemButton getLastItemButton(GUISegment s, int start){
		for(int i = start; i > 0; i--){
			if(buttonList.get(i) instanceof ItemButton){
				ItemButton lastButton = (ItemButton) buttonList.get(i);
				if (lastButton.segment == s){
					return lastButton;
				}
			}
		}
		return null;
	}
	
	private ItemButton getFirstItemButton(GUISegment s){
		for(GuiButton b : buttonList){
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
	 * creates and updates buttons on the screen, used to keep clutter out of the initGui method.
	 */
	private void createComponents(){
		boolean flag = false;
		int id = 0;
		int total = 0;
		int col = 0; //x
		int row = 0; //y
		
		addCustomButton(new EditButton(id++, width / 2 - 24, height / 2 - 26));
		addCustomButton(new AddButton(id, buttonList.get(id - 1).x + buttonList.get(id - 1).width, buttonList.get(id - 1).y, Top));
		id++;
		addCustomButton(new DeleteButton(id, buttonList.get(id - 1).x + buttonList.get(id - 1).width, buttonList.get(id - 1).y));
		id++;
		addCustomButton(new ArrowButton(id++, 0,0, EAST, Top));
		addCustomButton(new ArrowButton(id++, 0,0, WEST, Top));
		addCustomButton(new EditButton(id++, width / 2 - 24, height - 39));
		addCustomButton(new AddButton(id, buttonList.get(id - 1).x + buttonList.get(id - 1).width, buttonList.get(id - 1).y, Bottom));
		id++;
		addCustomButton(new DeleteButton(id, buttonList.get(id - 1).x + buttonList.get(id - 1).width, buttonList.get(id - 1).y));
		id++;
		addCustomButton(new ArrowButton(id++, 0,0, EAST, Bottom));
		addCustomButton(new ArrowButton(id++, 0,0, WEST, Bottom));
		addCustomButton(new AddButton(id++, 3, 18, GUISegment.Sidebar));
		addCustomButton(new DeleteButton(id, xPadding - 16 - 24, 18));
		addCustomButton(new ArrowButton(id++, 0,0, NORTH, Sidebar));
		addCustomButton(new ArrowButton(id++, 0,0, SOUTH, Sidebar));
		
		for(Blacklist b : Blacklist.getBlacklists().values()) {
			if(b != null) {
				addCustomButton(new BlacklistButton(id++, row, b));
				row++;
			}
			else {
				throw new IllegalStateException("Blacklists cannot be null!");
			}
		}
		
		row = 0;
		total = 0;
		
		for(ListItem l : Blacklist.getActiveBlacklist().getBannedIds()){
			if (l != null){
				if(!l.isOre()){
					if (total != 0 && total % 3 == 0){
						row = 0;
						col++;
					}
					int realcol = col - topPage;
					buttonList.add(new ItemButton(id++, xPadding + (((realcol * 16) + (realcol * ItemButton.xPadding))), yPadding + (row * 16) + (row * ItemButton.yPadding), col, row, l));
					row++;
					total++;
				}
				else {
					throw new AssertionError("OreDictionary value exists in ID only list! (" + l.getDataAsString() + ")");
				}
			}
			else{
				throw new IllegalStateException("List items cannot be null!");
			}
		}
		total = 0;
		col = 0;
		row = 0;
		for(ListItem l : Blacklist.getActiveBlacklist().getBannedOres()) {
			if(l != null) {
				if(l.isOre()) {
					if(total != 0 && total % 3 == 0){
						row = 0;
						col++;
					}
					int realcol = col - bottomPage;
					buttonList.add(new ItemButton(id++, xPadding + (((realcol * 16) + (realcol * ItemButton.xPadding))), (height/2 + yPadding) + (row * 16) + (row * ItemButton.yPadding), col, row, l));
					row++;
					total++;
				}
				else {
					throw new AssertionError("OreDictionary value exists in ID only list! (" + l.getDataAsString() + ")");
				}
			}
			else{
				throw new IllegalStateException("List items cannot be null!");
			}
		}
		
	}
	
	private void clear(){
		buttonList.clear();
	}
	
	private GuiButton addCustomButton(GuiButton button){
		buttonList.add(button);
		return button;
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if(button.visible) {
			if (button instanceof ItemButton){
				if (mode == Delete && Blacklist.getActiveBlacklist().removeFromBlacklist(((ItemButton) button).getListItem())){
					buttonList.remove(button);
					button = null;
					clear();
					initGui();
				}
				else if (mode == Edit){
					GUIAddToBlacklist.passedData = ((ItemButton) button).getListItem();
					mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_WILD, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
				}
			}
			else if (button instanceof ImageButton){
				((ImageButton) button).onClick();
			}
			else if (button instanceof BlacklistButton) {
				((BlacklistButton) button).onClick();
			}
		}
	}
	
	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			((BlacklistButton)this.buttonList.get(id + 1)).confirmDelete();
		}
		Minecraft.getMinecraft().displayGuiScreen(this);
	}
	
	private int getVisibleCount(GUISegment s){
		int ret = 0;
		if(s == Top){
			for(GuiButton b : buttonList){
				if (b instanceof ItemButton && ((ItemButton)b).segment == Top && b.visible){
					ret++;
				}
			}
			return (int)Math.ceil((double)ret / 3d);
		}
		else if(s == Bottom){
			for(GuiButton b : buttonList){
				if (b instanceof ItemButton && ((ItemButton)b).segment == Bottom && b.visible){
					ret++;
				}
			}
			return (int)Math.ceil((double)ret / 3d);
		}
		
		else if(s == Sidebar){
			for(GuiButton b : buttonList){
				if (b instanceof BlacklistButton && ((BlacklistButton)b).getGUISegment() == Sidebar && b.visible){
					ret++;
				}
			}
			return ret;
		}
		throw new AssertionError();
	}
	
	private int getTotalCount(GUISegment s){
		int ret = 0;
		if(s == Top){
			for(GuiButton b : buttonList){
				if (b instanceof ItemButton && ((ItemButton)b).segment == Top){
					ret++;
				}
			}
			return (int)Math.ceil((double)ret / 3d);
		}
		else if(s == Bottom) {
			for(GuiButton b : buttonList) {
				if(b instanceof ItemButton && ((ItemButton)b).segment == Bottom) {
					ret++;
				}
			}
			return (int)Math.ceil((double)ret / 3d);
		}
		
		else if(s == Sidebar){
			for(GuiButton b : buttonList){
				if (b instanceof BlacklistButton && ((BlacklistButton)b).getGUISegment() == Sidebar){
					ret++;
				}
			}
			return ret;
		}
		throw new AssertionError();
	}
	
	private final class ItemButton extends GuiButton{
		static final int xPadding = 5;
		static final int yPadding = 5;
		
		private ListItem item;
		private final GUISegment segment;
		public final int column;
		public final int row;
		
		public ItemButton(int buttonId, int x, int y, int col, int row, ListItem l) {
			super(buttonId, x, y, 16, 16, "");
			item = l;
			if (item.isOre()){
				segment = Bottom;
			}
			else{
				segment = Top;
			}
			column = col;
			this.row = row;
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float unknown){
			assert item != null;
			if (this.visible){
				RenderHelper.enableGUIStandardItemLighting();
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
			if(seg == Top){
				mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_ID, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
			else if (seg == Bottom){
				mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_ORE, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
			else if(seg == Sidebar) {
				mc.player.openGui(Main.getInstance(), GUI_BLACKLIST_ADD_BLACKLIST, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
		}

		@Override
		public GUISegment getGUISegment() {
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
				case Top: 
					switch (direction){
						case EAST: 
							topPage++; 
							break;
						case WEST: 
							topPage--; 
							break;
						default: 
							throw new IllegalStateException();
					}
					break;
				case Bottom:
					switch (direction) {
						case EAST:
							bottomPage++;
							break;
						case WEST:
							bottomPage--;
							break;
						default:
							throw new IllegalStateException();
					}
					break;
				case Sidebar:
					switch (direction) {
						case NORTH:
							sidePage--;
							break;
						case SOUTH:
							sidePage++;
							break;
						default:
							throw new IllegalStateException();
					}
					break;
				default: throw new AssertionError();
			}
		}

		@Override
		public GUISegment getGUISegment() {
			return seg;
		}
		
		public void updatePosition(){
			if (seg == Sidebar){
				this.x = xPadding - width - 48;
				if(direction == NORTH) {
					this.y = 18;
				}
				if(direction == SOUTH) {
					this.y = GUIBlacklist.this.height - yPadding;
				}
			}
			else{
				if (this.direction == EAST){
					x = (getVisibleCount(getGUISegment()) * 16 + (getVisibleCount(getGUISegment()) * ItemButton.xPadding)) + xPadding;
				}
				else{
					x = xPadding - width - ItemButton.xPadding;
				}
				y = (2 * 16 + (3 * ItemButton.yPadding) + yPadding);
				this.y -= this.height;
				if(this.getGUISegment() == Bottom) {
					this.y += GUIBlacklist.super.height/2;
				}
			}
		}
	}
	
	private final class BlacklistButton extends EditScreen.BlacklistButton{
		public final int row;
		private final Blacklist blacklist;

		public BlacklistButton(int id, int row, Blacklist blacklist) {
			super(id, 0, 16, blacklist.getName());
			this.row = row;
			this.blacklist = blacklist;
			if(this.blacklist == Blacklist.getActiveBlacklist()) {
				this.displayString = TextFormatting.YELLOW + "" + TextFormatting.BOLD + this.displayString;
			}
		}
		
		public void updatePosition() {
			this.width = xPadding - 10 - 16;
			if(this.getGUISegment() == Sidebar) {
				this.x = xPadding - width - 5 - 18;
				this.y = (18 * (row + 1) + (-sidePage * 18)) + 18;
				if(this.y + height> GUIBlacklist.this.height - yPadding || this.y + height < yPadding + 18) {
					this.visible = false;
				}
				else {
					this.visible = true;
				}
			}
			else {
				throw new AssertionError("Blacklist buttons cannot exist outside of the sidebar!");
			}
		}
		
		public void onClick() {
			if(GUIBlacklist.this.mode == Delete) {
				if(Blacklist.getBlacklists().size() > 1) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(GUIBlacklist.this, "Delete the following blacklist:", blacklist.getName(), this.id));
				}
			}
			else {
				Blacklist.setActiveBlacklist(this.blacklist);
				Minecraft.getMinecraft().player.openGui(Main.MODID, GUIHandler.GUI_BLACKLIST, Minecraft.getMinecraft().world, 0, 0, 0);
			}
		}
		
		private final void confirmDelete() {
			Blacklist.removeBlacklist(this.blacklist);
		}

		@Override
		public GUISegment getGUISegment() {
			return Sidebar;
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
