package me.mcblueparrot.client.mixin.client;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.mcblueparrot.client.util.Utils;
import v1_8_9.net.minecraft.client.resources.ResourcePackRepository;

@Mixin(ResourcePackRepository.Entry.class)
public class MixinResourcePackRepositoryEntry {

	@Redirect(method = "toString()Ljava/lang/String;", at = @At(value = "INVOKE", target = "Ljava/io/File;getName()Ljava/lang/String;"))
	public String getPackName(File instance) {
		return Utils.getRelativeToPackFolder(instance);
	}

}
