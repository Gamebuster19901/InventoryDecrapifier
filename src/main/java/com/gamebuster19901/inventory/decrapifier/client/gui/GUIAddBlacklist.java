package com.gamebuster19901.inventory.decrapifier.client.gui;

import java.awt.Color;
import java.io.IOException;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.client.management.Blacklist;
import com.gamebuster19901.inventory.decrapifier.proxy.ClientProxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GUIAddBlacklist extends GuiScreen{
	private GuiTextField nameField;
	
	private final String addBlacklistString = I18n.format("blacklist.instruction.add.blacklist");
	private final String cancelString = I18n.format("blacklist.instruction.cancel");
	
	public GUIAddBlacklist() {
		super();
	}
	
	@Override
	public void initGui() {
		super.initGui();
		nameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		nameField.setCanLoseFocus(false);
		nameField.setFocused(true);
		nameField.setMaxStringLength(100);
		nameField.setText(new Blacklist(false).getName());
		setNameFieldColor();
	}
	
	@Override
	public void updateScreen() {
		nameField.updateCursorCounter();
		super.updateScreen();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		fontRenderer.drawString(addBlacklistString, width / 2 - fontRenderer.getStringWidth(addBlacklistString) / 2, 60 - fontRenderer.FONT_HEIGHT * 2, Color.white.getRGB());
		fontRenderer.drawString(cancelString, width / 2 - fontRenderer.getStringWidth(cancelString) / 2, 60 - fontRenderer.FONT_HEIGHT, Color.white.getRGB());
		nameField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void keyTyped(char c, int i) throws IOException{
		if(c == '\n' | c == '\r' | i == 28) {
			if(!Blacklist.nameTaken(nameField.getText())) {
				new Blacklist(nameField.getText());
				ClientProxy.syncToFile();
				mc.player.openGui(Main.MODID, GUIHandler.GUI_BLACKLIST, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
			}
		}
		if(i == 1) {
			mc.player.openGui(Main.MODID, GUIHandler.GUI_BLACKLIST, mc.player.world, (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ);
		}
		else {
			nameField.textboxKeyTyped(c, i);
			setNameFieldColor();
		}
	}
	
	private void setNameFieldColor() {
		if(Blacklist.nameTaken(nameField.getText())) {
			nameField.setTextColor(Color.yellow.hashCode());
		}
		else {
			nameField.setTextColor(Color.green.hashCode());
		}
	}
}
