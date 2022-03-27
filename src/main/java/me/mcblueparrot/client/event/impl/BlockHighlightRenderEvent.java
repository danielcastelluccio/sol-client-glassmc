package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;
import v1_8_9.net.minecraft.util.MovingObjectPosition;

@RequiredArgsConstructor
public class BlockHighlightRenderEvent {

	public final MovingObjectPosition movingObjectPosition;
	public final float partialTicks;
	public boolean cancelled;

}
