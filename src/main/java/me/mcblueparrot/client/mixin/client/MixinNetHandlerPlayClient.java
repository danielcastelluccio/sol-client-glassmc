package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.EntityDamageEvent;
import me.mcblueparrot.client.event.impl.ReceiveChatMessageEvent;
import me.mcblueparrot.client.mixin.client.access.AccessGuiScreen;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.GuiIngame;
import v1_8_9.net.minecraft.client.gui.GuiNewChat;
import v1_8_9.net.minecraft.client.gui.inventory.GuiContainer;
import v1_8_9.net.minecraft.client.multiplayer.WorldClient;
import v1_8_9.net.minecraft.client.network.NetHandlerPlayClient;
import v1_8_9.net.minecraft.network.play.server.S19PacketEntityStatus;
import v1_8_9.net.minecraft.network.play.server.S2EPacketCloseWindow;
import v1_8_9.net.minecraft.network.play.server.S3FPacketCustomPayload;
import v1_8_9.net.minecraft.util.EnumChatFormatting;
import v1_8_9.net.minecraft.util.IChatComponent;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

	@Shadow
	private Minecraft gameController;

	@Shadow
	private WorldClient clientWorldController;

	@Inject(method = "handleCustomPayload(Lv1_8_9/net/minecraft/network/play/server/S3FPacketCustomPayload;)V", at = @At("RETURN"))
	public void handleCustomPayload(S3FPacketCustomPayload payload, CallbackInfo callback) {
		Client.INSTANCE.bus.post(payload); // Post as normal event object
	}

	@Inject(method = "handleEntityStatus(Lv1_8_9/net/minecraft/network/play/server/S19PacketEntityStatus;)V", at = @At("RETURN"))
	public void handleEntityStatus(S19PacketEntityStatus packetIn, CallbackInfo callback) {
		if(packetIn.getOpCode() == 2) {
			Client.INSTANCE.bus.post(new EntityDamageEvent(packetIn.getEntity(clientWorldController)));
		}
	}

	@Redirect(method = "handleChat(Lv1_8_9/net/minecraft/network/play/server/S02PacketChat;)V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/gui/GuiNewChat;printChatMessage(Lv1_8_9/net/minecraft/util/IChatComponent;)V"))
	public void handleChat(GuiNewChat guiNewChat, IChatComponent chatComponent) {
		if(!Client.INSTANCE.bus.post(new ReceiveChatMessageEvent(false,
				EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText()), false)).cancelled) {
			guiNewChat.printChatMessage(chatComponent);
		}
	}

	@Redirect(method = "handleChat(Lv1_8_9/net/minecraft/network/play/server/S02PacketChat;)V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/gui/GuiIngame;setRecordPlaying(Lv1_8_9/net/minecraft/util/IChatComponent;Z)V"))
	public void handleActionBar(GuiIngame guiIngame, IChatComponent component, boolean isPlaying) {
		if(!Client.INSTANCE.bus.post(new ReceiveChatMessageEvent(true,
				EnumChatFormatting.getTextWithoutFormattingCodes(component.getUnformattedText()), false)).cancelled) {
			guiIngame.setRecordPlaying(component, isPlaying);
		}
	}

	@Inject(method = "handleCloseWindow(Lv1_8_9/net/minecraft/network/play/server/S2EPacketCloseWindow;)V", at = @At("HEAD"), cancellable = true)
	public void handleCloseWindow(S2EPacketCloseWindow packetIn, CallbackInfo callback) {
		if(gameController.currentScreen != null && !(/*((AccessGuiScreen) gameController.currentScreen).canBeForceClosed()*/ true || gameController.currentScreen instanceof GuiContainer)) {
			callback.cancel();
		}
	}

}
