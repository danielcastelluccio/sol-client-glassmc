package me.mcblueparrot.client.mixin.client.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import v1_8_9.net.minecraft.client.renderer.entity.Render;
import v1_8_9.net.minecraft.entity.Entity;

@Mixin(Render.class)
public interface AccessRender<T extends Entity> {

	@Invoker("renderName(Lv1_8_9/net/minecraft/entity/Entity;DDD)V")
	void doRenderName(T entity, double x, double y, double z);

}
