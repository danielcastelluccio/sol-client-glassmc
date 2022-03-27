package me.mcblueparrot.client.mixin.client.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

@Mixin(GuiScreen.class)
public interface AccessGuiScreen {

	//@Accessor
	//boolean canBeForceClosed();

}
