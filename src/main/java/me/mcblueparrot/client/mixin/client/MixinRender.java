package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import me.mcblueparrot.client.mixin.client.access.AccessRender;
import v1_8_9.net.minecraft.client.renderer.entity.Render;
import v1_8_9.net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {

}
