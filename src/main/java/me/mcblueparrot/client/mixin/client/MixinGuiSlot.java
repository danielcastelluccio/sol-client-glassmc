package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import v1_8_9.net.minecraft.client.gui.GuiSlot;

@Mixin(GuiSlot.class)
public class MixinGuiSlot {

	@ModifyConstant(method = "handleMouseInput()V", constant = @Constant(intValue = 2, ordinal = 4))
	public int getScrollDivisor(int original) {
		return 1;
	}

}
