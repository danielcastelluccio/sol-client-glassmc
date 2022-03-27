package me.mcblueparrot.client.mixin.client.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import v1_8_9.net.minecraft.entity.EntityLivingBase;

@Mixin(EntityLivingBase.class)
public interface AccessEntityLivingBase {

	@Invoker("getArmSwingAnimationEnd()I")
	int accessArmSwingAnimationEnd();

}
