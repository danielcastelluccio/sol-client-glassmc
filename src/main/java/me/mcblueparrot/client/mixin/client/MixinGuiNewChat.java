package me.mcblueparrot.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.ChatRenderEvent;
import me.mcblueparrot.client.mod.impl.hud.chat.ChatMod;
import me.mcblueparrot.client.mixin.client.access.AccessGuiNewChat;
import me.mcblueparrot.client.mixin.client.access.AccessMinecraft;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.ChatLine;
import v1_8_9.net.minecraft.client.gui.GuiNewChat;
import v1_8_9.net.minecraft.util.IChatComponent;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

	@Inject(at = @At("HEAD"), cancellable = true, method = "drawChat(I)V")
	public void drawChat(int updateCounter, CallbackInfo callback) {
		if (Client.INSTANCE.bus.post(new ChatRenderEvent((GuiNewChat) (Object) /* hax */ this, updateCounter,
				((AccessMinecraft) Minecraft.getMinecraft()).getTimerSC().renderPartialTicks)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = "printChatMessage(Lv1_8_9/net/minecraft/util/IChatComponent;)V")
	public void allowNullMessage(IChatComponent component, CallbackInfo callback) {
		if(component == null) {
			callback.cancel();
		}
	}

	@Redirect(method = "setChatLine(Lv1_8_9/net/minecraft/util/IChatComponent;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
	public int getSize(List instance) {
		if(ChatMod.enabled && ChatMod.instance.infiniteChat) {
			return 0;
		}

		return instance.size();
	}

	@Shadow
	@Final
	private List<ChatLine> drawnChatLines;

	@Shadow
	@Final
	private List<ChatLine> chatLines;

}
