package me.mcblueparrot.client.mixin.client;

import java.io.File;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.mcblueparrot.client.util.Utils;
import v1_8_9.net.minecraft.client.resources.AbstractResourcePack;

@Mixin(AbstractResourcePack.class)
public class MixinAbstractResourcePack {

	@Overwrite
	public String getPackName() {
		return Utils.getRelativeToPackFolder(resourcePackFile);
	}

	@Shadow
	@Final
	protected File resourcePackFile;

}
