package me.mcblueparrot.client.mixin.client.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import v1_8_9.net.minecraft.client.gui.GuiMainMenu;

@Mixin(GuiMainMenu.class)
public interface AccessGuiMainMenu {

	@Invoker("drawPanorama(IIF)V")
	void renderPanorama(int mouseX, int mouseY, float partialTicks);

	@Invoker("rotateAndBlurSkybox(F)V")
	void rotateAndBlurPanorama(float partialTicks);

}
