package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import v1_8_9.com.mojang.authlib.GameProfile;

import me.mcblueparrot.client.CapeManager;
import me.mcblueparrot.client.Client;
import v1_8_9.net.minecraft.client.entity.AbstractClientPlayer;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;
import v1_8_9.net.minecraft.util.ResourceLocation;
import v1_8_9.net.minecraft.world.World;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends EntityPlayer {

	public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {
		super(worldIn, gameProfileIn);
	}

	@Inject(method = "getLocationCape()Lv1_8_9/net/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	public void overrideCapeLocation(CallbackInfoReturnable<ResourceLocation> callback) {
		CapeManager manager = Client.INSTANCE.getCapeManager();

		if(manager == null) {
			return;
		}

		ResourceLocation cape = manager.getForPlayer(this);

		if(cape != null) {
			callback.setReturnValue(cape);
		}
	}

}
