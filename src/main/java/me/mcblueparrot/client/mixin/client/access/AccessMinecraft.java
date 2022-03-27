package me.mcblueparrot.client.mixin.client.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.resources.DefaultResourcePack;
import v1_8_9.net.minecraft.client.resources.data.IMetadataSerializer;
import v1_8_9.net.minecraft.util.Timer;


// For some reason you do need a surrogate duck.
@Mixin(Minecraft.class)
public interface AccessMinecraft {

	@Accessor
	boolean isRunning();

	@Accessor(value = "timer")
	Timer getTimerSC();

	@Accessor("mcDefaultResourcePack")
	DefaultResourcePack getDefaultResourcePack();

	@Accessor("metadataSerializer_")
	IMetadataSerializer getMetadataSerialiser();

}
