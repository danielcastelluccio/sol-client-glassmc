package me.mcblueparrot.client.ui.element;

import me.mcblueparrot.client.mixin.client.access.AccessGuiChat;
import me.mcblueparrot.client.util.Utils;
import v1_8_9.net.minecraft.client.Minecraft;

import javax.rmi.CORBA.Util;

public interface ChatButton {

	int getPriority();
	
	default int getWidth() {
		return Minecraft.getMinecraft().fontRendererObj.getStringWidth(getText()) + 4;
	}

	int getPopupWidth();

	int getPopupHeight();

	String getText();

	void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY);

	default boolean isOpen() {
		AccessGuiChat chat = (AccessGuiChat) Utils.getChatGui();
		return false;//chat != null && chat.getSelectedChatButton() == this;
	}

}
