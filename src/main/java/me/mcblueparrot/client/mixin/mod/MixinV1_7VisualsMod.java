package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.mod.impl.V1_7VisualsMod;
import me.mcblueparrot.client.mixin.client.access.AccessEntityLivingBase;
import me.mcblueparrot.client.mixin.client.access.AccessMinecraft;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.renderer.EntityRenderer;
import v1_8_9.net.minecraft.client.renderer.ItemRenderer;
import v1_8_9.net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import v1_8_9.net.minecraft.entity.Entity;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;
import v1_8_9.net.minecraft.util.MovingObjectPosition;

public abstract class MixinV1_7VisualsMod {

	@Mixin(ItemRenderer.class)
	public static abstract class MixinItemRenderer {

		@Shadow protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

		@Shadow @Final private Minecraft mc;

		@Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"))
		public void allowUseAndSwing(ItemRenderer itemRenderer, float equipProgress, float swingProgress) {
			transformFirstPersonItem(equipProgress,
					swingProgress == 0.0F && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine ?
					mc.thePlayer.getSwingProgress(((AccessMinecraft) Minecraft.getMinecraft()).getTimerSC().renderPartialTicks) :
							swingProgress);
		}

	}

	@Mixin(EntityRenderer.class)
	public static abstract class MixinEntityRenderer {

		private float eyeHeightSubtractor;
		private long lastEyeHeightUpdate;

		@Inject(method = "renderHand(FI)V", at = @At(value = "HEAD"))
		public void forceSwing(float partialTicks, int xOffset, CallbackInfo callback) {
			if(mc.thePlayer != null && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
					&& mc.objectMouseOver != null
					&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
					&& mc.gameSettings.keyBindAttack.isKeyDown() && mc.gameSettings.keyBindUseItem.isKeyDown()
					&& mc.thePlayer.getItemInUseCount() > 0 && (!mc.thePlayer.isSwingInProgress
					|| mc.thePlayer.swingProgressInt >= ((AccessEntityLivingBase) mc.thePlayer).accessArmSwingAnimationEnd()
					/ 2 || mc.thePlayer.swingProgressInt < 0)) {
				mc.thePlayer.swingProgressInt = -1;
				mc.thePlayer.isSwingInProgress = true;
			}
		}

		@Redirect(method = "orientCamera(F)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/entity/Entity;getEyeHeight()F"))
		public float smoothSneaking(Entity entity) {
			if(V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking
					&& entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				float height = player.getEyeHeight();
				if(player.isSneaking()) {
					height += 0.08F;
				}
				float actualEyeHeightSubtractor = player.isSneaking() ? 0.08F : 0;
				long sinceLastUpdate = System.currentTimeMillis() - lastEyeHeightUpdate;
				lastEyeHeightUpdate = System.currentTimeMillis();
				if(actualEyeHeightSubtractor > eyeHeightSubtractor) {
					eyeHeightSubtractor += sinceLastUpdate / 500f;
					if(actualEyeHeightSubtractor < eyeHeightSubtractor) {
						eyeHeightSubtractor = actualEyeHeightSubtractor;
					}
				}
				else if(actualEyeHeightSubtractor < eyeHeightSubtractor) {
					eyeHeightSubtractor -= sinceLastUpdate / 500f;
					if(actualEyeHeightSubtractor > eyeHeightSubtractor) {
						eyeHeightSubtractor = actualEyeHeightSubtractor;
					}
				}
				return height - eyeHeightSubtractor;
			}
			return entity.getEyeHeight();
		}

		@Shadow private Minecraft mc;

	}

	@Mixin(LayerArmorBase.class)
	public static class MixinLayerArmorBase {

		@Inject(method = "shouldCombineTextures()Z", at = @At("HEAD"), cancellable = true)
		public void oldArmour(CallbackInfoReturnable<Boolean> callback) {
			if(V1_7VisualsMod.enabled && V1_7VisualsMod.instance.armourDamage) {
				callback.setReturnValue(true);
			}
		}

	}

}
