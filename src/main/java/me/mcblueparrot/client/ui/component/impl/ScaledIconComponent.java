package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import v1_8_9.net.minecraft.client.gui.Gui;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.util.ResourceLocation;

public class ScaledIconComponent extends ColouredComponent {

	private Controller<String> iconName;
	private int width;
	private int height;

	public ScaledIconComponent(String iconName, int width, int height) {
		this((component, defaultName) -> iconName, width, height, (component, defaultColour) -> defaultColour);
	}

	public ScaledIconComponent(String iconName, int width, int height, Controller<Colour> colour) {
		this((component, defaultName) -> iconName, width, height, colour);
	}

	public ScaledIconComponent(Controller<String> iconName, int width, int height, Controller<Colour> colour) {
		super(colour);
		this.iconName = iconName;
		this.width = width;
		this.height = height;
	}

	public void renderFallback(ComponentRenderInfo info) {
	}

	public boolean useFallback() {
		return false;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		if(useFallback() && !SolClientMod.instance.roundedUI) {
			renderFallback(info);
		}
		else {
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();

			Utils.glColour(getColour());

			mc.getTextureManager().bindTexture(new ResourceLocation(
					"textures/gui/" + iconName.get(this, "sol_client_confusion") + "_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
		}

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, width, height);
	}

}
