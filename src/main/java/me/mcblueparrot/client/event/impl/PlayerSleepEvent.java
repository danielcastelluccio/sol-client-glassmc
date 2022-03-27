package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;
import v1_8_9.net.minecraft.util.BlockPos;

@AllArgsConstructor
public class PlayerSleepEvent {

	public EntityPlayer entityPlayer;
	public BlockPos pos;

}
