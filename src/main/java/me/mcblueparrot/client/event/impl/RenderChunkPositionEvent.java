package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import v1_8_9.net.minecraft.client.renderer.chunk.RenderChunk;
import v1_8_9.net.minecraft.util.BlockPos;

@AllArgsConstructor
public class RenderChunkPositionEvent {

	public RenderChunk chunk;
	public BlockPos position;

}
