package me.mcblueparrot.client.mixin.client;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.ui.element.ChatButton;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.mixin.client.access.AccessGuiChat;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import v1_8_9.net.minecraft.client.gui.Gui;
import v1_8_9.net.minecraft.client.gui.GuiChat;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends GuiScreen {

	private ChatButton selectedButton;
	private boolean wasMouseDown;

	@Redirect(method = "drawScreen(IIF)V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/gui/GuiChat;drawRect(IIIII)V"))
	public void addChatButtons(int left, int top, int right, int bottom, int color, int mouseX, int mouseY,
							   float partialTicks) {
		boolean mouseDown = Mouse.isButtonDown(0);

		List<ChatButton> buttons = Client.INSTANCE.getChatButtons();
		for(ChatButton button : buttons) {
			int start = right - button.getWidth();
			Rectangle buttonBounds = new Rectangle(start, height - 14, button.getWidth(), 12);
			Utils.drawRectangle(buttonBounds, buttonBounds.contains(mouseX, mouseY) ? Colour.WHITE_128 : Colour.BLACK_128);
			fontRendererObj.drawString(button.getText(),
					start + (button.getWidth() / 2) - (fontRendererObj.getStringWidth(button.getText()) / 2),
					this.height - 8 - (fontRendererObj.FONT_HEIGHT / 2),
					buttonBounds.contains(mouseX, mouseY) ? 0 : -1);
			if(mouseDown && !wasMouseDown && buttonBounds.contains(mouseX, mouseY)) {
				if(selectedButton == button) {
					Utils.playClickSound(false);
					selectedButton = null;
				}
				else {
					Utils.playClickSound(false);
					selectedButton = button;
				}
			}
			if(selectedButton == button) {
				button.render(right - button.getPopupWidth(), this.height - 15 - button.getPopupHeight(),
						mouseDown, wasMouseDown,
						mouseDown && !wasMouseDown,
						mouseX, mouseY);
			}
			right = start - 1;
		}

		Gui.drawRect(left, top, right, bottom, color);

		wasMouseDown = mouseDown;
	}

}
