package me.mcblueparrot.client.ui.screen;

//import com.replaymod.replay.ReplayModReplay;
//import com.replaymod.replay.gui.screen.GuiReplayViewer;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.impl.SolClientMod;
//import me.mcblueparrot.client.mod.impl.replay.SCReplayMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.component.impl.ButtonType;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.mixin.client.access.AccessGuiMainMenu;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.Gui;
import v1_8_9.net.minecraft.client.gui.GuiLanguage;
import v1_8_9.net.minecraft.client.gui.GuiMainMenu;
import v1_8_9.net.minecraft.client.gui.GuiMultiplayer;
import v1_8_9.net.minecraft.client.gui.GuiOptions;
import v1_8_9.net.minecraft.client.gui.GuiSelectWorld;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.client.renderer.Tessellator;
import v1_8_9.net.minecraft.client.renderer.WorldRenderer;
import v1_8_9.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import v1_8_9.net.minecraft.util.ResourceLocation;

public class SolClientMainMenu extends Screen {

	private GuiMainMenu base;
	private boolean wasMouseDown;
	private boolean mouseDown;

	public SolClientMainMenu(GuiMainMenu base) {
		super(new MainMenuComponent());
		this.base = base;
		background = false;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		base.setWorldAndResolution(mc, width, height);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawPanorama(mouseX, mouseY, partialTicks);

		Font font = SolClientMod.getFont();

		String copyrightString = "Copyright Mojang AB. Do not distribute!";
		font.renderString(copyrightString, (int) (width - font.getWidth(copyrightString) - 10), height - 15, -1);
		String versionString = "Minecraft 1.8.9";
		font.renderString(versionString, (int) (width - font.getWidth(versionString) - 10), height - 25, -1);

		font.renderString("Copyright TheKodeToad and contributors.", 10, height - 15, -1);
		font.renderString(Client.NAME, 10, height - 25, -1);

		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_logo_with_text_" +
						Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(width / 2 - 64, 50, 0, 0, 128, 32, 128, 32);
//
//		Button singleplayerButton = new Button(SolClientMod.getFont(), "Singleplayer",
//				new Rectangle(width / 2 - 100, height / 4 + 48, 200, 20), SolClientMod.instance.uiColour, SolClientMod.instance.uiHover)
//				.withIcon("textures/gui/sol_client_player");
//		Button multiplayerButton = new Button(SolClientMod.getFont(), "Multiplayer",
//				new Rectangle(width / 2 - 100, height / 4 + 48 + 25, 200, 20), SolClientMod.instance.uiColour, SolClientMod.instance.uiHover)
//				.withIcon("textures/gui/sol_client_players");
//
//		if(singleplayerButton.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
//			Utils.playClickSound(true);
//			mc.displayGuiScreen(new GuiSelectWorld(this));
//		}
//		else if(multiplayerButton.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
//			Utils.playClickSound(true);
//			mc.displayGuiScreen(new GuiMultiplayer(this));
//		}
//
//		singleplayerButton.render(mouseX, mouseY);
//		multiplayerButton.render(mouseX, mouseY);
//
//		boolean replay = SCReplayMod.enabled;
//		int buttonsCount = 3;
//
//		if(replay) {
//			buttonsCount++;
//		}
//
//		int buttonsX = width / 2 - (12 * buttonsCount);
//
//		Button languageButton = new Button(SolClientMod.getFont(), "",
//				new Rectangle(buttonsX, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
//				SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_language");
//		Button optionsButton = new Button(SolClientMod.getFont(), "",
//				new Rectangle(buttonsX += 26, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
//				SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_settings_small");
//		Button modsButton = new Button(SolClientMod.getFont(), "",
//				new Rectangle(buttonsX += 26, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
//				SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_mods");
//
//		Button replayButton = null;
//
//		if(replay) {
//			replayButton = new Button(SolClientMod.getFont(), "",
//					new Rectangle(buttonsX += 26, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
//					SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_replay_button");
//		}
//
//		if(mouseDown && !wasMouseDown) {
//			if(optionsButton.contains(mouseX, mouseY)) {
//				Utils.playClickSound(true);
//				mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
//			}
//			else if(modsButton.contains(mouseX, mouseY)) {
//				Utils.playClickSound(true);
//				mc.displayGuiScreen(new ModsScreen());
//			}
//			else if(languageButton.contains(mouseX, mouseY)) {
//				Utils.playClickSound(true);
//				mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
//			}
//			else if(replayButton != null && replayButton.contains(mouseX, mouseY)) {
//				Utils.playClickSound(true);
//				new GuiReplayViewer(ReplayModReplay.instance).display();
//			}
//		}
//
//		languageButton.render(mouseX, mouseY);
//		optionsButton.render(mouseX, mouseY);
//		modsButton.render(mouseX, mouseY);
//
//		if(replayButton != null) {
//			replayButton.render(mouseX, mouseY);
//		}

		wasMouseDown = mouseDown;

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
		AccessGuiMainMenu access = (AccessGuiMainMenu) (Object) base;

		this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        access.renderPanorama(mouseX, mouseY, partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        mc.getFramebuffer().bindFramebuffer(true);

        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);

        float uvBase = width > height ? 120.0F / width : 120.0F / height;
        float uBase = height * uvBase / 256.0F;
        float vBase = width * uvBase / 256.0F;

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		renderer.pos(0.0D, height, zLevel).tex((0.5F - uBase), (0.5F + vBase)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		renderer.pos(width, height, zLevel).tex(0.5F - uBase, 0.5F - vBase).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		renderer.pos(width, 0.0D, zLevel).tex(0.5F + uBase, 0.5F - vBase).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		renderer.pos(0.0D, 0.0D, zLevel).tex(0.5F + uBase, 0.5F + vBase).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();

		drawRect(0, 0, width, height, new Colour(0, 0, 0, 100).getValue());

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		base.updateScreen();
	}

	private static class MainMenuComponent extends Component {

		private int buttonsX;

		public MainMenuComponent() {
			Controller<Colour> defaultColourController =
					(component, defaultColour) -> component.isHovered() ? SolClientMod.instance.uiHover
							: SolClientMod.instance.uiColour;

			add(new ButtonComponent((component, defaultText) -> "Singleplayer",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_player")
							.type(ButtonType.LARGE).onClick((info, button) -> {
								if (button == 0) {
									Utils.playClickSound(true);
									mc.displayGuiScreen(new GuiSelectWorld(screen));
									return true;
								}

								return false;
							}),
					(component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 48,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "Multiplayer", new AnimatedColourController(defaultColourController))
					.withIcon("sol_client_players").type(ButtonType.LARGE).onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiMultiplayer(screen));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 73,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_language").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiLanguage(screen, mc.gameSettings, mc.getLanguageManager()));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> {
						int buttonsCount = 3;

						//if(SCReplayMod.enabled) {
						//	buttonsCount++;
						//}

						buttonsX = screen.width / 2 - (12 * buttonsCount);

						return new Rectangle(buttonsX, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_settings_small").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiOptions(screen, mc.gameSettings));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 26, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_mods").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new ModsScreen());
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 52, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			/*add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_replay_button")
							.type(ButtonType.SMALL).onClick((info, button) -> {
								if (button == 0) {
									Utils.playClickSound(true);
									//new GuiReplayViewer(ReplayModReplay.instance).display();
									return true;
								}

								return false;
							}).visibilityController((component, defaultVisibility) -> SCReplayMod.enabled),
					(component, defaultBounds) -> new Rectangle(buttonsX + 78, screen.height / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));*/
		}

	}

}
