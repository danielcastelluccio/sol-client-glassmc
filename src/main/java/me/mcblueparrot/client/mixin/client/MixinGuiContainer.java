package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import v1_8_9.net.minecraft.client.gui.inventory.GuiContainer;

/**
 * @reason Allow mouse hotkeys in container GUI.
 */
@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

	@Inject(method = "mouseClicked(III)V", at = @At(value = "FIELD",
			target = "Lv1_8_9/net/minecraft/client/gui/inventory/GuiContainer;ignoreMouseUp:Z"), cancellable = true)
	public void allowMouseInput(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		if(checkHotbarKeys(mouseButton - 100)) {
			callback.cancel();
		}
	}

	@Shadow
	protected abstract boolean checkHotbarKeys(int keyCode);
	
}
