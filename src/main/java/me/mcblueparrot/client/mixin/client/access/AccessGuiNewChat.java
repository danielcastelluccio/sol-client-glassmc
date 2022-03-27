package me.mcblueparrot.client.mixin.client.access;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import v1_8_9.net.minecraft.client.gui.ChatLine;
import v1_8_9.net.minecraft.client.gui.GuiNewChat;

@Mixin(GuiNewChat.class)
public interface AccessGuiNewChat {

	@Accessor
	List<ChatLine> getDrawnChatLines();

	@Accessor
	boolean getIsScrolled();

	@Accessor
	int getScrollPos();

}
