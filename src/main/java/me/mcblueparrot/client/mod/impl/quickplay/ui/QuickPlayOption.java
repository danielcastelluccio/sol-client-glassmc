package me.mcblueparrot.client.mod.impl.quickplay.ui;

import me.mcblueparrot.client.mod.impl.quickplay.QuickPlayMod;
import v1_8_9.net.minecraft.item.ItemStack;

public interface QuickPlayOption {

	String getText();

	void onClick(QuickPlayPalette palette, QuickPlayMod mod);

	ItemStack getIcon();

}
