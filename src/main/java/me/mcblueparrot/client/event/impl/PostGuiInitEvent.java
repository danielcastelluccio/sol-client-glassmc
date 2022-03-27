package me.mcblueparrot.client.event.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import v1_8_9.net.minecraft.client.gui.GuiButton;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

@AllArgsConstructor
public class PostGuiInitEvent {

	public GuiScreen screen;
	public final Collection<GuiButton> buttonList;

}
