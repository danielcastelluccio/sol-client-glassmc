package me.mcblueparrot.client.mixin.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.Client;
import v1_8_9.net.minecraft.client.resources.FallbackResourceManager;
import v1_8_9.net.minecraft.client.resources.IResource;
import v1_8_9.net.minecraft.client.resources.IResourcePack;
import v1_8_9.net.minecraft.client.resources.SimpleReloadableResourceManager;
import v1_8_9.net.minecraft.util.ResourceLocation;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager {

	@Inject(method = "getResource(Lv1_8_9/net/minecraft/util/ResourceLocation;)Lv1_8_9/net/minecraft/client/resources/IResource;", at = @At("HEAD"), cancellable = true)
	public void getResource(ResourceLocation location, CallbackInfoReturnable<IResource> callback) {
		if(Client.INSTANCE.getResource(location) != null) {
			callback.setReturnValue(Client.INSTANCE.getResource(location));
		}
	}

	@Inject(method = "reloadResources(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/resources/" +
			"SimpleReloadableResourceManager;notifyReloadListeners()V", shift = At.Shift.BEFORE))
	public void injectDomains(List<IResourcePack> resourcesPacksList, CallbackInfo callback) {
		for(String domain : new String[] {
				"replaymod",
				"jgui"
		}) {
			setResourceDomains.add(domain);
			domainResourceManagers.put(domain, domainResourceManagers.get("minecraft"));
		}
	}

	@Final
	@Shadow
	private Set<String> setResourceDomains;

	@Final
	@Shadow
	private Map<String, FallbackResourceManager> domainResourceManagers;

}
