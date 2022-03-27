package me.mcblueparrot.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.EntityAttackEvent;
import me.mcblueparrot.client.event.impl.PlayerSleepEvent;
import v1_8_9.net.minecraft.entity.Entity;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;
import v1_8_9.net.minecraft.util.BlockPos;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {

	@Inject(method = "attackTargetEntityWithCurrentItem(Lv1_8_9/net/minecraft/entity/Entity;)V", at = @At("HEAD"))
	public void attackEntity(Entity entity, CallbackInfo callback) {
		if(entity.canAttackWithItem()) {
			Client.INSTANCE.bus.post(new EntityAttackEvent(entity));
		}
	}

	@Inject(method = "trySleep(Lv1_8_9/net/minecraft/util/BlockPos;)Lv1_8_9/net/minecraft/entity/player/EntityPlayer$EnumStatus;", at = @At("HEAD"))
	public void onSleep(BlockPos pos, CallbackInfoReturnable<EntityPlayer.EnumStatus> callback) {
		Client.INSTANCE.bus.post(new PlayerSleepEvent((EntityPlayer) (Object) this, pos));
	}

}
