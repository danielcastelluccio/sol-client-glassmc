package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.SoundPlayEvent;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.audio.PositionedSoundRecord;
import v1_8_9.net.minecraft.client.multiplayer.WorldClient;
import v1_8_9.net.minecraft.util.ResourceLocation;

@Mixin(WorldClient.class)
public class MixinWorldClient {

	@Shadow @Final private Minecraft mc;

	@Inject(method = "playSound(DDDLjava/lang/String;FFZ)V", at = @At(value = "HEAD"), cancellable = true)
	public void handlePlaySound(double x, double y, double z, String soundName, float volume, float pitch,
								boolean distanceDelay, CallbackInfo callback) {
		SoundPlayEvent event = Client.INSTANCE.bus.post(new SoundPlayEvent(soundName, volume, pitch, volume, pitch));
		if(event.pitch != event.originalPitch || event.volume != event.originalVolume) {
			callback.cancel();
			volume = event.volume;
			pitch = event.pitch;
			double distanceSq = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
			PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName),
					volume, pitch, (float) x, (float) y, (float) z);

			if(distanceDelay && distanceSq > 100.0D) {
				mc.getSoundHandler().playDelayedSound(positionedsoundrecord,
						(int) (Math.sqrt(distanceSq) / 40.0D * 20.0D));
			}
			else {
				mc.getSoundHandler().playSound(positionedsoundrecord);
			}
		}
	}

}
