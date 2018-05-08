package com.gamebuster19901.inventory.decrapifier.client.gui;

import static com.gamebuster19901.inventory.decrapifier.client.gui.GUIHandler.GUI_BLACKLIST;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.management.Blacklist;
import com.gamebuster19901.inventory.decrapifier.client.management.ListItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class GUIAddToBlacklist extends EditScreen{
	
	volatile static ListItem passedData = null; //used to pass data to the constructor, because forge was designed poorly
	
	private static final Field stackSize = ReflectionHelper.findField(ItemStack.class, new String[]{"stackSize", "field_179550_j"});
	private final boolean isOre;
	private boolean hasRegistered = false; // a flag to tell if the screen needs to be updated, false means it needs to update
	private ArrayList<GuiButton> components = new ArrayList<GuiButton>(); //A list of GuiButtons used to update GuiScreen.buttonList
	private GuiTextField idNotice;
	private GuiTextField idField;
	private GuiTextField metaNotice;
	private GuiTextField metaMin;
	private GuiTextField metaMax;
	private ListItem parentListItem;
	
	private final String addOreString = I18n.format("blacklist.instruction.add.ore");
	private final String addItemString = I18n.format("blacklist.instruction.add.id");
	private final String selectString = I18n.format("blacklist.instruction.add.select");
	
	private static Minecraft getMC(){
		return Minecraft.getMinecraft();
	}
	
	public GUIAddToBlacklist(){
		super();
		if (passedData == null){
			isOre = false;
		}
		else{
			isOre = passedData.isOre();
			parentListItem = passedData;
			passedData = null;
			initGui();
		}
	};
	
	GUIAddToBlacklist(boolean isOre){
		super();
		passedData = null;
		this.isOre = isOre;
		initGui();
	}
	
	@Override
	public void initGui(){
		hasRegistered = false;
		buttonList.clear();
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void updateScreen(){
		constructComponents();
		if(!hasRegistered){
			buttonList.clear();
			for(GuiButton component : components){
				if(component != null){
					buttonList.add(component);
				}
			}
			hasRegistered = true;
		}
		idField.updateCursorCounter();
		super.updateScreen();
		//ffdebug("3");
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		
		this.drawDefaultBackground();
		//this.idField.drawTextBox();
		if(isOre){
			fontRenderer.drawString(addOreString, width / 2 - fontRenderer.getStringWidth(addOreString) / 2, 60 - fontRenderer.FONT_HEIGHT * 2, Color.white.getRGB());
		}
		else{
			fontRenderer.drawString(addItemString, width / 2 - fontRenderer.getStringWidth(addItemString) / 2, 60 - fontRenderer.FONT_HEIGHT * 2, Color.white.getRGB());
		}
		fontRenderer.drawString(selectString, width / 2 - fontRenderer.getStringWidth(selectString) / 2, 60 - fontRenderer.FONT_HEIGHT, Color.white.getRGB());
		idField.drawTextBox();
		for(GuiButton component : components){
			if(component != null && component instanceof ItemButton){
				((ItemButton)component).drawButton(mc, mouseX, mouseY, 0f);
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
		//debug("4" + " " + components.size());
	}
	
	private int id;
	/**
	 * creates and updates buttons on the screen, used to keep clutter out of the updateScreen method.
	 */
	private void constructComponents(){
		mc = Minecraft.getMinecraft();
		clear();
		int row = 3;
		int col = 0;
		ArrayList<ItemStack> fullInventory = new ArrayList<ItemStack>();
		fullInventory.addAll(mc.player.inventory.mainInventory);
		fullInventory.addAll(mc.player.inventory.armorInventory);
		fullInventory.addAll(mc.player.inventory.offHandInventory);
		for(ItemStack i : mc.player.inventory.mainInventory){
			if (col % 9 == 0 && col != 0){
				if (row == 3){
					row = 0;
				}
				else{
					row++;
				}
				col = 0;
			}
			if (!i.isEmpty()){
				addCustomButton(new ItemButton(id, this.width / 32 + (((col * 16) + (col * ItemButton.xPadding))), this.height / 2 + ((row * 16) + (row * ItemButton.yPadding)), i));
				//debug("" + id + ", " + this.width / 32 + (((col * 16) + (col * ItemButton.xPadding))) + ", " +  this.height / 2 + ((row * 16) + (row * ItemButton.yPadding)));
			}
			col++;
		}
		
		if(!hasRegistered){
			idField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
			idField.setCanLoseFocus(false);
			idField.setFocused(true);
			idField.setMaxStringLength(100);
			if (parentListItem == null){
				if(isOre){
					idField.setText(addOreString);
				}
				else{
					idField.setText(addItemString);
				}
			}
			else{
				idField.setText(parentListItem.toString());
				setIDFieldColor();
			}
			idField.setVisible(true);
		}
		NonNullList<ItemStack> inv = mc.player.inventoryContainer.getInventory();
		//debug("5");
	}
	
	private GuiButton addCustomButton(GuiButton button){
		buttonList.add(button);
		components.add(button);
		id++;
		return button;
	}
	
	private void clear(){
		components.clear();
		buttonList.clear();
		id = 0;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		super.actionPerformed(button);
		if (button instanceof ItemButton){
			if (isOre){
				if (OreDictionary.getOreIDs(((ItemButton) button).getItem()).length != 0){
					idField.setText(OreDictionary.getOreName(OreDictionary.getOreIDs(((ItemButton) button).getItem())[0]));
					setIDFieldColor();
				}
			}
			else{
				if (((ItemButton)button).getItem().getItemDamage() == 0){
					idField.setText(Item.REGISTRY.getNameForObject(((ItemButton)button).getItem().getItem()).toString());
				}
				else{
					idField.setText(Item.REGISTRY.getNameForObject(((ItemButton)button).getItem().getItem()).toString() + "[" + ((ItemButton)button).getItem().getItemDamage() + "]");
				}
				setIDFieldColor();
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.idField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void keyTyped(char c, int i) throws IOException{
 		super.keyTyped(c, i);
 		if (c == '\n' | c == '\r' | i == 28){
 			if (Blacklist.getActiveBlacklist().addToBlacklist(ListItem.fromString(idField.getText(), this.isOre), true)){
 				if (this.parentListItem == null || Blacklist.getActiveBlacklist().removeFromBlacklist(parentListItem)){
 					mc.player.openGui(Main.getInstance(), GUI_BLACKLIST, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
 				}
 				else{
 					throw new AssertionError("Blacklist GUI was instantiated with \"" + parentListItem + "\" but it couldn't be removed from the blacklist! This should never happen!");
 				}
 			}
 		}
		if(idField.getText().equals(addOreString) || idField.getText().equals(addItemString)){
			idField.setText("");
		}
		if (c != ' '){
			idField.textboxKeyTyped(c, i);
		}
		else{
			idField.textboxKeyTyped('\b', i);
			idField.textboxKeyTyped('_', 12);
		}
		setIDFieldColor();
	}
	
	public void setIDFieldColor(){
		if(ListItem.fromString(idField.getText(), isOre) != null){
			if (Blacklist.getActiveBlacklist().contains(ListItem.fromString(idField.getText(), isOre))){
				idField.setTextColor(Color.yellow.hashCode());
			}
			else{
				idField.setTextColor(Color.green.hashCode());
			}
		}
		else{
			idField.setTextColor(Color.red.hashCode());
		}
	}
	
	private final class ItemButton extends GuiButton{
		static final int xPadding = 5;
		static final int yPadding = 5;
		
		private ItemStack item;
		
		public ItemButton(int buttonId, int x, int y, ItemStack i) {
			super(buttonId, x, y, 16, 16, "");
			item = i;
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float unknown){
			assert item != null;
			GL11.glDisable(GL11.GL_LIGHTING);
			drawItem();
		}
		
		private ItemStack getItem(){
			return item;
		}
		
		private void drawItem(){
			itemRender.renderItemAndEffectIntoGUI(item, x, y);
			try {
				itemRender.renderItemOverlayIntoGUI(fontRenderer, item, x, y, stackSize.get(item).toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new AssertionError(e);
			} catch (NullPointerException e) {
				try {
					stackSize.get(item);
				} catch(NullPointerException | IllegalArgumentException | IllegalAccessException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}
