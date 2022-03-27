package me.mcblueparrot.client.ui.screen.mods;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.hud.HudElement;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.screen.SolClientMainMenu;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.GuiMainMenu;
import v1_8_9.net.minecraft.client.gui.GuiScreen;

public class MoveHudsScreen extends Screen {

	private GuiScreen title;
	private HudElement movingHud;
	private Position moveOffset;

	public MoveHudsScreen() {
		super(new MoveHudsComponent());
		background = false;
		if(parentScreen instanceof Screen) {
			GuiScreen grandparentScreen = ((Screen) parentScreen).getParentScreen();

			if(grandparentScreen instanceof GuiMainMenu || grandparentScreen instanceof SolClientMainMenu) {
				title = grandparentScreen;
			}
		}
	}

	public HudElement getSelectedHud(int mouseX, int mouseY) {
		for(HudElement hud : Client.INSTANCE.getHuds()) {
			if(!(hud.isEnabled() && hud.isVisible())) continue;

			if(hud.isSelected(mouseX, mouseY)) {
				return hud;
			}
		}
		return null;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 1) {
			for(HudElement hud : Client.INSTANCE.getHuds()) {
				if(hud.isEnabled() && hud.isVisible() && hud.getMultipliedBounds() != null && hud.getMultipliedBounds()
						.contains(mouseX, mouseY)) {
					if(parentScreen instanceof ModsScreen) {
						Utils.playClickSound(true);

						((ModsScreen) parentScreen).switchMod(hud.getMod());

						Minecraft.getMinecraft().displayGuiScreen(parentScreen);
					}
				}
			}
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);

		if(title != null) title.setWorldAndResolution(mc, width, height);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if(title != null) title.updateScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(title != null) {
			title.drawScreen(0, 0, partialTicks);
		}

		HudElement selectedHud = getSelectedHud(mouseX, mouseY);
		if(Mouse.isButtonDown(0)) {
			if(movingHud == null) {
				if(selectedHud != null) {
					movingHud = selectedHud;
					moveOffset = new Position(selectedHud.getPosition().getX() - mouseX,
							selectedHud.getPosition().getY() - mouseY);
				}
			}
			else {
				movingHud.setPosition(new Position(mouseX + moveOffset.getX(), mouseY + moveOffset.getY()));
			}
		}
		else {
			movingHud = null;
		}

		for(HudElement hud : Client.INSTANCE.getHuds()) {
			if(!(hud.isEnabled() && hud.isVisible())) continue;

			if(mc.theWorld == null) {
				hud.render(true);
			}

			Rectangle bounds = hud.getMultipliedBounds();
			if(bounds != null) {
				bounds.stroke(SolClientMod.instance.uiColour);
			}
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if(keyCode == 1 || keyCode == SolClientMod.instance.editHudKey.getKeyCode()) {
			Client.INSTANCE.save();
			if(title != null) {
				mc.displayGuiScreen(title);
			}
			else {
				mc.displayGuiScreen(null);
			}
		}
	}

	public static class MoveHudsComponent extends Component {

		public MoveHudsComponent() {
			add(ButtonComponent.done(() -> screen.close()),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 30,
									defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

	}

}
