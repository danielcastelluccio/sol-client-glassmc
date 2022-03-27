package me.mcblueparrot.client.mod.impl;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostProcessingEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mixin.client.access.AccessShaderGroup;
import v1_8_9.net.minecraft.client.resources.IResource;
import v1_8_9.net.minecraft.client.resources.data.IMetadataSection;
import v1_8_9.net.minecraft.client.shader.ShaderGroup;
import v1_8_9.net.minecraft.client.shader.ShaderUniform;
import v1_8_9.net.minecraft.util.ResourceLocation;
import v1_8_9.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ColourSaturationMod extends Mod implements PrimaryIntegerSettingMod {

	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation("minecraft:shaders/post/" +
			"color_convolve.json");
	@Expose
	@ConfigOption("Saturation")
	@Slider(min = 0, max = 2F, step = 0.1F)
	private float saturation = 1f;
	private ShaderGroup group;
	private float groupSaturation;

	public ShaderGroup getGroup() {
		return group;
	}

	public ColourSaturationMod() {
		super("Colour Saturation", "colour_saturation", "Change the saturation of ingame colours.",
				ModCategory.VISUAL);
		Client.INSTANCE.addResource(RESOURCE_LOCATION, new SaturationShader());
	}

	public void update() {
		if(group == null) {
			groupSaturation = saturation;
			try {
				group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), RESOURCE_LOCATION);
				group.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
			}
			catch(JsonSyntaxException | IOException error) {
				logger.error("Could not load saturation shader", error);
			}
		}
		if(groupSaturation != saturation) {
			((AccessShaderGroup) group).getListShaders().forEach((shader) -> {
				ShaderUniform saturationUniform = shader.getShaderManager().getShaderUniform("Saturation");
				if(saturationUniform != null) {
					saturationUniform.set(saturation);
				}
			});
			groupSaturation = saturation;
		}
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		update();
		event.groups.add(getGroup());
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		group = null;
	}

	@Override
	public void decrement() {
		saturation = Math.max(0, saturation - 0.1F);
	}

	@Override
	public void increment() {
		saturation = Math.min(2, saturation + 0.1F);
	}

	public class SaturationShader implements IResource {

		@Override
		public ResourceLocation getResourceLocation() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			return IOUtils.toInputStream(String.format("{" +
					"    \"targets\": [" +
					"        \"swap\"," +
					"        \"previous\"" +
					"    ]," +
					"    \"passes\": [" +
					"        {" +
					"            \"name\": \"color_convolve\"," +
					"            \"intarget\": \"minecraft:main\"," +
					"            \"outtarget\": \"swap\"," +
					"            \"auxtargets\": [" +
					"                {" +
					"                    \"name\": \"PrevSampler\"," +
					"                    \"id\": \"previous\"" +
					"                }" +
					"            ]," +
					"            \"uniforms\": [" +
					"                {" +
					"                    \"name\": \"Saturation\"," +
					"                    \"values\": [ %s ]" +
					"                }" +
					"            ]" +
					"        }," +
					"        {" +
					"            \"name\": \"blit\"," +
					"            \"intarget\": \"swap\"," +
					"            \"outtarget\": \"previous\"" +
					"        }," +
					"        {" +
					"            \"name\": \"blit\"," +
					"            \"intarget\": \"swap\"," +
					"            \"outtarget\": \"minecraft:main\"" +
					"        }" +
					"    ]" +
					"}", saturation, saturation, saturation));
		}

		@Override
		public boolean hasMetadata() {
			return false;
		}

		@Override
		public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
			return null;
		}

		@Override
		public String getResourcePackName() {
			return null;
		}

	}

}
