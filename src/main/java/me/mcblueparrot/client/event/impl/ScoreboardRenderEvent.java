package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;
import v1_8_9.net.minecraft.client.gui.ScaledResolution;
import v1_8_9.net.minecraft.scoreboard.ScoreObjective;

@RequiredArgsConstructor
public class ScoreboardRenderEvent {

	public final ScoreObjective objective;
	public final ScaledResolution scaledRes;
	public boolean cancelled;

}
