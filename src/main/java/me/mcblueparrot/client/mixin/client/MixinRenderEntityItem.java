package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.ItemEntityRenderEvent;
import v1_8_9.net.minecraft.client.renderer.entity.RenderEntityItem;
import v1_8_9.net.minecraft.client.resources.model.IBakedModel;
import v1_8_9.net.minecraft.entity.item.EntityItem;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem {

	@Inject(method = "func_177077_a(Lv1_8_9/net/minecraft/entity/item/EntityItem;DDDFLv1_8_9/net/minecraft/client/resources/model/IBakedModel;)I", at = @At(value = "HEAD"), cancellable = true)
	public void preItemEntityRender(EntityItem itemIn, double x, double y, double z, float partialTicks, IBakedModel model, CallbackInfoReturnable<Integer> callback) {
		int result;
		if((result = Client.INSTANCE.bus.post(new ItemEntityRenderEvent(itemIn, x, y, z, partialTicks, model)).result) != -1) {
			callback.setReturnValue(result);
		}
	}

}
