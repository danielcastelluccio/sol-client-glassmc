
package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import com.replaymod.replay.ReplayModReplay;

import me.mcblueparrot.client.ui.screen.IngameServerList;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import v1_8_9.net.minecraft.client.gui.GuiButton;
import v1_8_9.net.minecraft.client.gui.GuiIngameMenu;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

	@Inject(method = "initGui()V", at = @At("RETURN"))
	public void addButtons(CallbackInfo callback) {
		boolean replay = false;//ReplayModReplay.instance.getReplayHandler() != null;

		buttonList.add(new GuiButton(5000, replay ? buttonList.get(2).xPosition + 102 : width / 2 - 100,
				replay ? buttonList.get(2).yPosition : height / 4 + 56
				, 98, 20,
				"Mods"));

		if(!replay) {
			buttonList.add(new GuiButton(5001, width / 2 + 2, height / 4 + 56, 98, 20,
					"Servers"));
		}
	}

	@Inject(method = "actionPerformed(Lv1_8_9/net/minecraft/client/gui/GuiButton;)V", at = @At("RETURN"))
	public void actionPerformed(GuiButton button, CallbackInfo callback) {
		if(button.id == 5000) {
			mc.displayGuiScreen(new ModsScreen());
		}
		else if(button.id == 5001) {
			mc.displayGuiScreen(new IngameServerList(this));
		}
	}

}
