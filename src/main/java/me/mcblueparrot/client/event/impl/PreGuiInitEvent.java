package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

@RequiredArgsConstructor
public class PreGuiInitEvent {

	public final GuiScreen screen;
	public boolean cancelled;

}
