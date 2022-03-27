package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import v1_8_9.net.minecraft.client.renderer.chunk.RenderChunk;

@AllArgsConstructor
public class PreRenderChunkEvent {

	public RenderChunk chunk;

}
