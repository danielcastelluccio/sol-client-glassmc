package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;
import v1_8_9.net.minecraft.client.resources.model.IBakedModel;
import v1_8_9.net.minecraft.entity.item.EntityItem;

@RequiredArgsConstructor
public class ItemEntityRenderEvent {
	
	public boolean cancelled;
	public final EntityItem entity;
	public final double x;
	public final double y;
	public final double z;
	public final float partialTicks;
	public final IBakedModel model;
	public int result = -1;

}
