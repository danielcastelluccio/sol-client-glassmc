package me.mcblueparrot.client.mod.impl.hud;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PreGameOverlayRenderEvent;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import v1_8_9.net.minecraft.client.gui.Gui;
import v1_8_9.net.minecraft.client.gui.ScaledResolution;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.util.MovingObjectPosition.MovingObjectType;
import v1_8_9.net.minecraft.util.ResourceLocation;
import v1_8_9.net.minecraft.world.WorldSettings.GameType;

public class CrosshairMod extends HudMod {

	private static final ResourceLocation CLIENT_CROSSHAIRS = new ResourceLocation("textures/gui" +
			"/sol_client_crosshairs.png");

	@Expose
	@ConfigOption("Style")
	private Type type = Type.DEFAULT;
	@Expose
	@ConfigOption("Third Person")
	private boolean thirdPerson = true;
	@Expose
	@ConfigOption("Spectator")
	private boolean spectatorAlways = false;
	@Expose
	@ConfigOption("Debug")
	private boolean debug = false;
	@Expose
	@ConfigOption("Blending")
	private boolean blending = true;
	@Expose
	@ConfigOption("Crosshair Colour")
	private Colour crosshairColour = Colour.WHITE;
	@Expose
	@ConfigOption("Highlight Entities")
	private boolean highlightEntities = false;
	@Expose
	@ConfigOption("Entity Colour")
	private Colour entityColour = Colour.RED;

	public CrosshairMod() {
		super("Crosshair", "crosshair", "Customise your crosshair.");
	}

	@EventHandler
	public void onCrosshairRender(PreGameOverlayRenderEvent event) {
		if(event.type == GameOverlayElement.CROSSHAIRS) {
			event.cancelled = true;
			if ((!debug && mc.gameSettings.showDebugInfo) ||
					(!spectatorAlways && mc.playerController.getCurrentGameType() == GameType.SPECTATOR && mc.objectMouseOver.typeOfHit != MovingObjectType.ENTITY) ||
					(!thirdPerson && mc.gameSettings.thirdPersonView != 0)) {
				return;
			}
			ScaledResolution resolution = new ScaledResolution(mc);
			int x = (int) (resolution.getScaledWidth() / getScale() / 2 - 7);
			int y = (int) (resolution.getScaledHeight() / getScale() / 2 - 7);

			GlStateManager.pushMatrix();
			GlStateManager.scale(getScale(), getScale(), getScale());

			Utils.glColour(crosshairColour);

			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

			if(highlightEntities && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && !(mc.objectMouseOver.entityHit.isInvisible()
					|| mc.objectMouseOver.entityHit.isInvisibleToPlayer(mc.thePlayer))) {
				Utils.glColour(entityColour);
			}
			else if(blending) {
				GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
				GlStateManager.enableAlpha();
			}

			if (type == Type.DEFAULT) {
				mc.getTextureManager().bindTexture(Gui.icons);
				Utils.drawTexture(x, y, 0, 0, 16, 16, 0);
			}
			else {
				mc.getTextureManager().bindTexture(CLIENT_CROSSHAIRS);
				int v = (type.ordinal() - 2) * 16;
				Utils.drawTexture(x, y, 0, v, 16, 16, 0);
				mc.getTextureManager().bindTexture(Gui.icons);
			}
			GL11.glColor4f(1, 1, 1, 1);

			GlStateManager.popMatrix();
		}
	}

	enum Type {
		DEFAULT("Default"),
		NONE("None"),
		DOT("Dot"),
		PLUS("Plus"),
		PLUS_DOT("Plus Dot"),
		SQUARE("Square"),
		SQUARE_DOT("Square Dot"),
		CIRCLE("Circle"),
		CIRCLE_DOT("Circle Dot"),
		FOUR_ANGLED("4 Angled"),
		FOUR_ANGLED_DOT("4 Angled Dot"),
		TRIANGLE("Triangle");

		private String name;

		Type(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
