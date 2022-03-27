package me.mcblueparrot.client.mixin.client.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import v1_8_9.net.minecraft.entity.Entity;

@Mixin(Entity.class)
public interface AccessEntity {

	@Accessor
	boolean getIsInWeb();

}
