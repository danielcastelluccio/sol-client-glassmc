package me.mcblueparrot.client.mod.impl.quickplay.ui;

import me.mcblueparrot.client.mod.impl.quickplay.QuickPlayMod;
import v1_8_9.net.minecraft.init.Items;
import v1_8_9.net.minecraft.item.ItemStack;

public class AllGamesOption implements QuickPlayOption {

	@Override
	public String getText() {
		return "All Games >";
	}

	@Override
	public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
		palette.openAllGames();
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.compass);
	}

}
