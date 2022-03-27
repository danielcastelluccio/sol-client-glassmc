package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.BlockHighlightRenderEvent;
import me.mcblueparrot.client.event.impl.CameraRotateEvent;
import me.mcblueparrot.client.event.impl.FovEvent;
import me.mcblueparrot.client.event.impl.GammaEvent;
import me.mcblueparrot.client.event.impl.PlayerHeadRotateEvent;
import me.mcblueparrot.client.event.impl.PostProcessingEvent;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.mixin.client.access.AccessMinecraft;
import v1_8_9.net.minecraft.block.material.Material;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.entity.EntityPlayerSP;
import v1_8_9.net.minecraft.client.renderer.EntityRenderer;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.client.renderer.OpenGlHelper;
import v1_8_9.net.minecraft.client.settings.GameSettings;
import v1_8_9.net.minecraft.client.shader.ShaderGroup;
import v1_8_9.net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

	@Redirect(method = "updateLightmap(F)V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;gammaSetting:F"))
	public float overrideGamma(GameSettings settings) {
		return Client.INSTANCE.bus.post(new GammaEvent(settings.gammaSetting)).gamma;
	}

	@Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/shader" +
			"/Framebuffer;bindFramebuffer(Z)V", shift = At.Shift.BEFORE))
	public void addShaders(float partialTicks, long nanoTime, CallbackInfo callback) {
		for(ShaderGroup group : Client.INSTANCE.bus.post(new PostProcessingEvent(PostProcessingEvent.Type.RENDER))
				.groups) {
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			group.loadShaderGroup(((AccessMinecraft) Minecraft.getMinecraft()).getTimerSC().renderPartialTicks);
			GlStateManager.popMatrix();
		}
	}

	@Inject(method = "updateShaderGroupSize(II)V", at = @At("RETURN"))
	public void updateShaders(int width, int height, CallbackInfo callback) {
		if(mc.theWorld == null) {
			return;
		}

		if(OpenGlHelper.shadersSupported) {
			for (ShaderGroup group : Client.INSTANCE.bus.post(new PostProcessingEvent(PostProcessingEvent.Type.UPDATE))
					.groups) {
				group.createBindFramebuffers(width, height);
			}
		}
	}

	// region Rotate Camera Event

	private static float rotationYaw;
	private static float prevRotationYaw;
	private static float rotationPitch;
	private static float prevRotationPitch;

	@Inject(method = "orientCamera(F)V", at = @At("HEAD"))
	public void orientCamera(float partialTicks, CallbackInfo callback) {
		rotationYaw = mc.getRenderViewEntity().rotationYaw;
		prevRotationYaw = mc.getRenderViewEntity().prevRotationYaw;
		rotationPitch = mc.getRenderViewEntity().rotationPitch;
		prevRotationPitch = mc.getRenderViewEntity().prevRotationPitch;
		float roll = 0;

		CameraRotateEvent event = Client.INSTANCE.bus.post(new CameraRotateEvent(rotationYaw, rotationPitch, roll));
		rotationYaw = event.yaw;
		rotationPitch = event.pitch;
		roll = event.roll;

		event = Client.INSTANCE.bus.post(new CameraRotateEvent(prevRotationYaw, prevRotationPitch, roll));
		prevRotationYaw = event.yaw;
		prevRotationPitch = event.pitch;
		GlStateManager.rotate(event.roll, 0, 0, 1);
	}

	@Redirect(method = "orientCamera(F)V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/entity/Entity;rotationYaw:F"))
	public float getRotationYaw(Entity entity) {
		return rotationYaw;
	}

	@Redirect(method = "orientCamera(F)V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/entity/Entity;prevRotationYaw:F"))
	public float getPrevRotationYaw(Entity entity) {
		return prevRotationYaw;
	}

	@Redirect(method = "orientCamera(F)V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/entity/Entity;" +
			"rotationPitch:F"))
	public float getRotationPitch(Entity entity) {
		return rotationPitch;
	}

	@Redirect(method = "orientCamera(F)V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/entity/Entity;" +
			"prevRotationPitch:F"))
	public float getPrevRotationPitch(Entity entity) {
		return prevRotationPitch;
	}

	// endregion

	@Redirect(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/entity/EntityPlayerSP;setAngles(FF)V"))
	public void lookinAround(EntityPlayerSP entityPlayerSP, float yaw, float pitch) {
		PlayerHeadRotateEvent event = new PlayerHeadRotateEvent(yaw, pitch);
		Client.INSTANCE.bus.post(event);
		yaw = event.yaw;
		pitch = event.pitch;
		if(!event.cancelled && !Utils.isSpectatingEntityInReplay()) {
			entityPlayerSP.setAngles(yaw, pitch);
		}
	}

	@Inject(method = "getFOVModifier(FZ)F", at = @At("RETURN"), cancellable = true)
	public void getFov(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> callback) {
		callback.setReturnValue(Client.INSTANCE.bus.post(new FovEvent(callback.getReturnValue(), partialTicks)).fov);
	}

	// region Block Highlight

	@Redirect(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer" +
			"/EntityRenderer;isDrawBlockOutline()Z"))
	public boolean overrideCanDraw(EntityRenderer renderer) {
		return true;
	}

	@Redirect(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/entity/Entity;isInsideOfMaterial(Lv1_8_9/net/minecraft/block/material/Material;)Z",
			ordinal = 0))
	public boolean overrideWetBlockHighlight(Entity entity, Material materialIn) {
		boolean maybeWould = entity.isInsideOfMaterial(materialIn);
		boolean would = maybeWould && isDrawBlockOutline();
		if(maybeWould && Client.INSTANCE.bus.post(new BlockHighlightRenderEvent(mc.objectMouseOver,
				((AccessMinecraft) Minecraft.getMinecraft()).getTimerSC().renderPartialTicks)).cancelled) {
			return false;
		}
		return would;
	}

	@Redirect(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/entity/Entity;isInsideOfMaterial(Lv1_8_9/net/minecraft/block/material/Material;)Z",
			ordinal = 1))
	public boolean overrideBlockHighlight(Entity entity, Material materialIn) {
		boolean totallyWouldNot = entity.isInsideOfMaterial(materialIn);
		boolean wouldNot = totallyWouldNot || !isDrawBlockOutline();
		if(!totallyWouldNot && Client.INSTANCE.bus.post(new BlockHighlightRenderEvent(mc.objectMouseOver,
				((AccessMinecraft) Minecraft.getMinecraft()).getTimerSC().renderPartialTicks)).cancelled) {
			return true;
		}
		return wouldNot;
	}

	// endregion

	@Shadow
	private Minecraft mc;

	@Shadow
	protected abstract boolean isDrawBlockOutline();

}
