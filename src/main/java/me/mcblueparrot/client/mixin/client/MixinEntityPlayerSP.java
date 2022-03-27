package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import v1_8_9.com.mojang.authlib.GameProfile;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.SendChatMessageEvent;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.entity.AbstractClientPlayer;
import v1_8_9.net.minecraft.client.entity.EntityPlayerSP;
import v1_8_9.net.minecraft.entity.player.EnumPlayerModelParts;
import v1_8_9.net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {

	public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	@Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo callback) {
		if(Client.INSTANCE.bus.post(new SendChatMessageEvent(message)).cancelled) {
			callback.cancel();
		}
	}

	@Override
	public boolean isWearing(EnumPlayerModelParts part) {
		return Minecraft.getMinecraft().gameSettings.getModelParts().contains(part);
	}

}
