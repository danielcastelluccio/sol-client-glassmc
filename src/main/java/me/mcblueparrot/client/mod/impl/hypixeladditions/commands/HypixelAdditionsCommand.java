package me.mcblueparrot.client.mod.impl.hypixeladditions.commands;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.command.CommandBase;

@RequiredArgsConstructor
public abstract class HypixelAdditionsCommand extends CommandBase {

	protected final HypixelAdditionsMod mod;
	protected Minecraft mc = Minecraft.getMinecraft();
	
}
