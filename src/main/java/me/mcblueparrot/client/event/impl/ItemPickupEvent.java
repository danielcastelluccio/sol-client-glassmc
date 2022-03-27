package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import v1_8_9.net.minecraft.entity.item.EntityItem;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;

@AllArgsConstructor
public class ItemPickupEvent {

	public EntityPlayer player;
	public EntityItem pickedUp;

}
