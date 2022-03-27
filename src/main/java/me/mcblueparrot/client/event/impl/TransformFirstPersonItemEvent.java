package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import v1_8_9.net.minecraft.item.ItemStack;

@AllArgsConstructor
public class TransformFirstPersonItemEvent {

	public ItemStack itemToRender;
	public float equipProgress;
	public float swingProgress;

}
