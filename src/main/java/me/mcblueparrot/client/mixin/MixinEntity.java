package me.mcblueparrot.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import lombok.Getter;
import lombok.Setter;
import me.mcblueparrot.client.culling.Cullable;
import me.mcblueparrot.client.mixin.client.access.AccessEntity;
import v1_8_9.net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class MixinEntity implements Cullable {

	@Getter
	@Setter
	private boolean culled;

}
