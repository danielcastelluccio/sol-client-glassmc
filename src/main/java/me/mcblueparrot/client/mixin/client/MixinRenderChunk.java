package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.RenderChunkPositionEvent;
import v1_8_9.net.minecraft.client.renderer.chunk.RenderChunk;
import v1_8_9.net.minecraft.client.renderer.vertex.VertexBuffer;
import v1_8_9.net.minecraft.util.BlockPos;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {

	@Redirect(method = "deleteGlResources()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer/vertex/" +
			"VertexBuffer;deleteGlBuffers()V"))
	public void cancelDelete(VertexBuffer instance) {
	}

	@Inject(method = "setPosition(Lv1_8_9/net/minecraft/util/BlockPos;)V", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new RenderChunkPositionEvent((RenderChunk) (Object) this, pos));
	}

}
