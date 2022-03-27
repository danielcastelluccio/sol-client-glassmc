package me.mcblueparrot.client.mixin.client;

//import me.mcblueparrot.client.mod.impl.replay.SCReplayMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import com.replaymod.replay.ReplayModReplay;
//import com.replaymod.replay.gui.screen.GuiReplayViewer;

import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.mixin.client.access.AccessGuiMainMenu;
import v1_8_9.net.minecraft.client.gui.GuiButton;
import v1_8_9.net.minecraft.client.gui.GuiMainMenu;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen implements AccessGuiMainMenu {

	@Inject(method = "addSingleplayerMultiplayerButtons(II)V", at = @At("RETURN"))
	public void getModsButton(int x, int y, CallbackInfo callback) {
		buttonList.remove(realmsButton);
		buttonList.add(new GuiButton(realmsButton.id, realmsButton.xPosition, realmsButton.yPosition, "Mods"));

		//if(SCReplayMod.enabled) {
		//	buttonList.add(new ReplayButton(15, realmsButton.xPosition + 202, realmsButton.yPosition));
		//}
	}

	@Redirect(method = "actionPerformed(Lv1_8_9/net/minecraft/client/gui/GuiButton;)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiMainMenu;" +
			"switchToRealms()V"))
	public void openModsMenu(GuiMainMenu guiMainMenu) {
		mc.displayGuiScreen(new ModsScreen());
	}

	@Inject(method = "actionPerformed(Lv1_8_9/net/minecraft/client/gui/GuiButton;)V", at = @At("RETURN"))
	public void openReplayMenu(GuiButton button, CallbackInfo callback) {
		if(button.id == 15) {
			//new GuiReplayViewer(ReplayModReplay.instance).display();
		}
	}

	@Shadow
	private GuiButton realmsButton;

	@Invoker("drawPanorama(IIF)V")
	@Override
	public abstract void renderPanorama(int mouseX, int mouseY, float partialTicks);

	@Invoker("rotateAndBlurSkybox(F)V")
	@Override
	public abstract void rotateAndBlurPanorama(float partialTicks);

}
