package me.mcblueparrot.client.mixin.client.access;

import me.mcblueparrot.client.ui.element.ChatButton;
import me.mcblueparrot.client.util.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import v1_8_9.net.minecraft.client.gui.GuiChat;

@Mixin(GuiChat.class)
public interface AccessGuiChat {

	@Invoker("keyTyped(CI)V")
	void type(char typedChar, int keyCode);

	//@Accessor
	//ChatButton getSelectedChatButton();

	//@Accessor
	//void setSelectedChatButton(ChatButton button);

}
