package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.TransformFirstPersonItemEvent;
import v1_8_9.net.minecraft.client.renderer.ItemRenderer;
import v1_8_9.net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Shadow private ItemStack itemToRender;

	@Inject(method = "transformFirstPersonItem(FF)V", at = @At("HEAD"))
	public void transformFirstPersonItem(float equipProgress, float swingProgress, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new TransformFirstPersonItemEvent(itemToRender, equipProgress, swingProgress));
	}

}
