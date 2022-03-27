package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.GuiResourcePackList;
import v1_8_9.net.minecraft.client.gui.GuiSlot;

@Mixin(GuiResourcePackList.class)
public abstract class MixinGuiResourcePackList extends GuiSlot {

	public MixinGuiResourcePackList(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
	}

	@Inject(method = "<init>()V", at = @At("RETURN"))
	public void overrideTop(CallbackInfo callback) {
		top += 16;
		height -= 16;
		setHasListHeader(false, 0);
	}

}
