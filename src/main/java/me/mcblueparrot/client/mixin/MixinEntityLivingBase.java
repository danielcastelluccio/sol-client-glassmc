package me.mcblueparrot.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.ItemPickupEvent;
import me.mcblueparrot.client.mixin.client.access.AccessEntityLivingBase;
import v1_8_9.net.minecraft.entity.Entity;
import v1_8_9.net.minecraft.entity.EntityLivingBase;
import v1_8_9.net.minecraft.entity.item.EntityItem;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

	@Invoker("getArmSwingAnimationEnd()I")
	public abstract int accessArmSwingAnimationEnd();

	@Inject(method = "onItemPickup(Lv1_8_9/net/minecraft/entity/Entity;I)V", at = @At("HEAD"))
	public void onItemPickup(Entity entity, int stackSize, CallbackInfo callback) {
		if(entity instanceof EntityItem && (Object) this instanceof EntityPlayer) {
			Client.INSTANCE.bus.post(new ItemPickupEvent((EntityPlayer) (Object) this, (EntityItem) entity));
		}
	}

}
