package me.mcblueparrot.client.mixin.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import v1_8_9.net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.ActionPerformedEvent;
import me.mcblueparrot.client.event.impl.PostGuiInitEvent;
import me.mcblueparrot.client.event.impl.PostGuiRenderEvent;
import me.mcblueparrot.client.event.impl.PreGuiInitEvent;
import me.mcblueparrot.client.event.impl.PreGuiMouseInputEvent;
import me.mcblueparrot.client.event.impl.RenderGuiBackgroundEvent;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.mixin.client.access.AccessGuiScreen;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.Gui;
import v1_8_9.net.minecraft.client.gui.GuiButton;
import v1_8_9.net.minecraft.client.gui.GuiScreen;
import v1_8_9.net.minecraft.client.gui.inventory.GuiContainer;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.util.ResourceLocation;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {

	public boolean canBeForceClosed() {
		return true;
	}

	@Redirect(method = "drawWorldBackground(I)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V"))
	public void getTopColour(GuiScreen guiScreen, int left, int top, int right, int bottom, int startColor,
						   int endColor) {
		if(!Client.INSTANCE.bus.post(new RenderGuiBackgroundEvent()).cancelled) {
			Utils.drawGradientRect(left, top, right, bottom, startColor, endColor);
		}
		else {
			Utils.drawGradientRect(left, top, right, bottom, 0, 0);
		}
	}

	@Redirect(method = "mouseClicked(III)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiButton;mousePressed(Lv1_8_9/net/minecraft/client/Minecraft;II)Z"))
	public boolean onActionPerformed(GuiButton instance, Minecraft mc, int mouseX, int mouseY) {
		return instance.mousePressed(mc,
				mouseX,
				mouseY) && !Client.INSTANCE.bus.post(new ActionPerformedEvent((GuiScreen) (Object) this, instance)).cancelled;
	}

	@Redirect(method = "setWorldAndResolution(Lv1_8_9/net/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui" +
			"/GuiScreen;initGui()V"))
	public void guiInit(GuiScreen instance) {
		if(!Client.INSTANCE.bus.post(new PreGuiInitEvent(instance)).cancelled) {
			instance.initGui();
			Client.INSTANCE.bus.post(new PostGuiInitEvent(instance, buttonList));
		}
	}

	@Inject(method = "drawScreen(IIF)V", at = @At("RETURN"))
	public void postGuiRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		GlStateManager.color(1, 1, 1, 1); // Prevent colour from leaking
		Client.INSTANCE.bus.post(new PostGuiRenderEvent(partialTicks));

		if(SolClientMod.instance.logoInInventory && (Object) this instanceof GuiContainer) {
			GlStateManager.enableBlend();

			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_logo_with_text_" +
							Utils.getTextureScale() + ".png"));

			Gui.drawModalRectWithCustomSizedTexture(width - 140, height - 40, 0, 0, 128, 32, 128, 32);
		}
	}

	@Redirect(method = "handleInput()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiScreen;" +
			"handleMouseInput()V"))
	public void handleMouseInput(GuiScreen instance) throws IOException {
		if(!Client.INSTANCE.bus.post(new PreGuiMouseInputEvent()).cancelled) {
			instance.handleMouseInput();
		}
	}

	// Temporarily disable - Replay Mod bug
//	@Redirect(method = "handleInput", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiScreen;" +
//			"handleKeyboardInput()V"))
//	public void handleKeyboardInput(GuiScreen instance) throws IOException {
//		if(!Client.INSTANCE.bus.post(new PreGuiKeyboardInputEvent()).cancelled) {
//			instance.handleKeyboardInput();
//		}
//	}

	// Fix options not saving when "esc" is pressed.
	@Redirect(method = "keyTyped(CI)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/Minecraft;displayGuiScreen(Lv1_8_9/net/minecraft/client/gui/GuiScreen;)V"))
	public void saveFirst(Minecraft instance, GuiScreen screen) throws IOException {
		for(GuiButton button : buttonList) {
			if(button.displayString.equals(I18n.format("gui.done"))) {
				actionPerformed(button);
			}
		}
		instance.displayGuiScreen(null);
	}

	@Overwrite
	private void openWebLink(URI uri) {
		Utils.sendLauncherMessage("openUrl", uri.toString());
	}

	@Shadow
	protected List<GuiButton> buttonList;

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Shadow
	protected abstract void actionPerformed(GuiButton button) throws IOException;

	@Shadow
	protected Minecraft mc;

}
