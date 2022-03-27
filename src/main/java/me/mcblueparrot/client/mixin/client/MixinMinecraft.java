package me.mcblueparrot.client.mixin.client;

import java.util.ConcurrentModificationException;

import me.mcblueparrot.client.event.impl.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import com.replaymod.core.versions.MCVer;
//import com.replaymod.replay.InputReplayTimer;

import lombok.SneakyThrows;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.mod.impl.TweaksMod;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.screen.SolClientMainMenu;
import me.mcblueparrot.client.ui.screen.SplashScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.mixin.client.access.AccessGuiNewChat;
import me.mcblueparrot.client.mixin.client.access.AccessMinecraft;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.*;
import v1_8_9.net.minecraft.client.multiplayer.*;
import v1_8_9.net.minecraft.client.particle.EffectRenderer;
import v1_8_9.net.minecraft.client.renderer.EntityRenderer;
import v1_8_9.net.minecraft.client.renderer.entity.RenderManager;
import v1_8_9.net.minecraft.client.renderer.texture.TextureManager;
import v1_8_9.net.minecraft.client.settings.GameSettings;
import v1_8_9.net.minecraft.client.settings.GameSettings.Options;
import v1_8_9.net.minecraft.client.settings.KeyBinding;
import v1_8_9.net.minecraft.util.ChatComponentText;
import v1_8_9.net.minecraft.util.EnumChatFormatting;
import v1_8_9.net.minecraft.util.Session;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

	private boolean debugPressed;
	private boolean cancelDebug;

	// Don't see the benefit of logging the session ID, apart from for hackers.
	// I have decompiled dodgy-looking Minecraft plugins, and seen code that takes advantage of this.
	// This is mainly for the crash reporting feature, as I don't want users accounts to be hacked.
	@Redirect(method = "<init>(Lv1_8_9/net/minecraft/client/main/GameConfiguration;)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/util/Session;getSessionID()Ljava/lang/String;"))
	public String censorSessionId(Session instance) {
		return "☃";
	}

	// Mojang has made some questionable decisions with their code.
	// https://github.com/Sk1erLLC/Patcher/blob/master/src/main/java/club/sk1er/patcher/mixins/performance/MinecraftMixin_OptimizedWorldSwapping.java
	@Redirect(method = "loadWorld(Lv1_8_9/net/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void noneOfYourGarbage() {

	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/Minecraft;initStream()V", shift = At.Shift.AFTER))
	public void init(CallbackInfo callback) {
		Client.INSTANCE.init();
		gameSettings.loadOptions();
	}


	@Inject(method = "startGame()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiMainMenu;<init>()V"))
	public void postStart(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PostGameStartEvent());
	}

	@Inject(method = "runTick()V", at = @At("HEAD"))
	public void preRunTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PreTickEvent());
	}

	@Inject(method = "runTick()V", at = @At("RETURN"))
	public void postRunTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PostTickEvent());
	}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
	public boolean next() {
		boolean next = Mouse.next();

		if(next && Mouse.getEventButtonState()) {
			if(Client.INSTANCE.bus.post(new MouseClickEvent(Mouse.getEventButton())).cancelled) {
				next = next(); // Skip
			}
		}

		return next;
	}

	@Inject(method = "runGameLoop()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.BEFORE))
	public void preRenderTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PreRenderTickEvent());
	}

	@Inject(method = "runGameLoop()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
	public void postRenderTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PostRenderTickEvent());
	}

	/**
	 * @author TheKodeToad
	 */
	@Overwrite
	public int getLimitFramerate() {
		if((theWorld == null && !(currentScreen instanceof Screen)) || !Display.isActive()) {
			return 30; // Only limit framerate to 30. This means there is no noticable lag spike.
		}

		return gameSettings.limitFramerate;
	}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/settings/KeyBinding;" +
			"isPressed()Z", ordinal = 2))
	public boolean cancelHotbarSwitch(KeyBinding instance) {
		return instance.isPressed() && !Utils.isSpectatingEntityInReplay();
	}

	/**
	 * Particles mod causes crashes.
	 * This seems better than trying to fix the synchronisation issues - that would cause performance issues.
	 */
	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/particle/EffectRenderer;updateEffects()V"))
	public void concurrencyHack(EffectRenderer effectRenderer) {
		try {
			effectRenderer.updateEffects();
		}
		catch(NullPointerException | ConcurrentModificationException ignored) {
		}
	}

	@Inject(method = "loadWorld(Lv1_8_9/net/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
	public void loadWorld(WorldClient world, String message, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new WorldLoadEvent(world));
	}

	@Inject(method = "displayGuiScreen(Lv1_8_9/net/minecraft/client/gui/GuiScreen;)V", at = @At(value = "HEAD"), cancellable = true)
	public void openGui(GuiScreen screen, CallbackInfo callback) {
		if(((screen == null && theWorld == null) || screen instanceof GuiMainMenu) && SolClientMod.instance.fancyMainMenu) {
			callback.cancel();
			displayGuiScreen(new SolClientMainMenu(screen == null ? new GuiMainMenu() : (GuiMainMenu) screen));
			return;
		}
		else if(screen instanceof SolClientMainMenu && !SolClientMod.instance.fancyMainMenu) {
			callback.cancel();
			displayGuiScreen(null);
			return;
		}

		Client.INSTANCE.bus.post(new OpenGuiEvent(screen));
		if(currentScreen == null && screen != null) {
			Client.INSTANCE.bus.post(new InitialOpenGuiEvent(screen));
		}
	}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
	public int onScroll() {
		int dWheel = Mouse.getEventDWheel();

		int divided = 0;

		if(dWheel != 0) {
			divided = dWheel / Math.abs(dWheel);

			if(Client.INSTANCE.bus.post(new ScrollEvent(dWheel)).cancelled) {
				dWheel = 0;
			}
			else if(Utils.isSpectatingEntityInReplay()) {
				dWheel = 0;
			}
			else {
				//InputReplayTimer.handleScroll(divided);
			}
		}

		if(dWheel != 0 && !playerController.isSpectatorMode()
				&& TweaksMod.enabled && !TweaksMod.instance.hotbarScrolling) {
			dWheel = 0;
		}

		return dWheel;
	}

	@Redirect(method = "createDisplay()V",
			at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
	public void overrideTitle(String oldTitle) {
		Display.setTitle(Client.NAME + " on " + oldTitle);
	}

	@Inject(method = "setServerData(Lv1_8_9/net/minecraft/client/multiplayer/ServerData;)V", at = @At("TAIL"))
	public void onDisconnect(ServerData serverDataIn, CallbackInfo callback) {
		if(serverDataIn == null) {
			Client.INSTANCE.onServerChange(null);
		}
	}

	@Shadow
	public GuiScreen currentScreen;

	@Shadow
	protected abstract void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException;

	@Shadow
	private TextureManager renderEngine;

	private boolean loading = true;

	// region Splash Screen Rendering

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/resources/SkinManager;<init>(Lv1_8_9/net/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
	public void splashSkinManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
	public void splashSaveLoader(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/audio/SoundHandler;<init>(Lv1_8_9/net/minecraft/client/resources/IResourceManager;Lv1_8_9/net/minecraft/client/settings/GameSettings;)V"))
	public void splashSoundHandler(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/audio/MusicTicker;<init>(Lv1_8_9/net/minecraft/client/Minecraft;)V"))
	public void splashMusicTicker(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/gui/FontRenderer;<init>(Lv1_8_9/net/minecraft/client/settings/GameSettings;Lv1_8_9/net/minecraft/util/ResourceLocation;Lv1_8_9/net/minecraft/client/renderer/texture/TextureManager;Z)V"))
	public void splashFontRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/util/MouseHelper;<init>()V"))
	public void splashMouseHelper(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
	public void splashTextureMap(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/resources/model/ModelManager;<init>(Lv1_8_9/net/minecraft/client/renderer/texture/TextureMap;)V"))
	public void splashModelManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/entity/RenderItem;<init>(Lv1_8_9/net/minecraft/client/renderer/texture/TextureManager;Lv1_8_9/net/minecraft/client/resources/model/ModelManager;)V"))
	public void splashRenderItem(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/entity/RenderManager;<init>(Lv1_8_9/net/minecraft/client/renderer/texture/TextureManager;Lv1_8_9/net/minecraft/client/renderer/entity/RenderItem;)V"))
	public void splashRenderManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/ItemRenderer;<init>(Lv1_8_9/net/minecraft/client/Minecraft;)V"))
	public void splashItemRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/EntityRenderer;<init>(Lv1_8_9/net/minecraft/client/Minecraft;Lv1_8_9/net/minecraft/client/resources/IResourceManager;)V"))
	public void splashEntityRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lv1_8_9/net/minecraft/client/renderer/BlockModelShapes;Lv1_8_9/net/minecraft/client/settings/GameSettings;)V"))
	public void splashBlockRenderDispatcher(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/renderer/RenderGlobal;<init>(Lv1_8_9/net/minecraft/client/Minecraft;)V"))
	public void splashRenderGlobal(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/gui/achievement/GuiAchievement;<init>(Lv1_8_9/net/minecraft/client/Minecraft;)V"))
	public void splashGuiAchivement(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/particle/EffectRenderer;<init>(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/renderer/texture/TextureManager;)V"))
	public void splashEffectRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame()V", at = @At(value = "INVOKE",
			target = "Lv1_8_9/net/minecraft/client/gui/GuiIngame;<init>(Lv1_8_9/net/minecraft/client/Minecraft;)V"))
	public void splashGuiIngame(CallbackInfo callback) {
		draw();
	}

	@SneakyThrows
	private void draw() {
		drawSplashScreen(renderEngine);
	}

	@Inject(method = "drawSplashScreen(Lv1_8_9/net/minecraft/client/renderer/texture/TextureManager;)V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/Minecraft;" +
			"updateDisplay()V", shift = At.Shift.BEFORE))
	public void drawProgress(TextureManager textureManagerInstance, CallbackInfo callback) {
		SplashScreen.INSTANCE.draw();
	}

	@Redirect(method = "updateFramebufferSize()V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/Minecraft;" +
			"entityRenderer:Lv1_8_9/net/minecraft/client/renderer/EntityRenderer;"))
	public EntityRenderer hideEntityRenderer(Minecraft instance) {
		if(loading) {
			return null;
		}

		return instance.entityRenderer;
	}

	// endregion

	// region Better F3
	// Backport 1.9 F3 fixes

	@Redirect(method = "runTick()V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;" +
			"showDebugInfo:Z",
			ordinal = 1))
	public void preventDefaultF3(GameSettings settings, boolean f3) {
	}

	@Inject(method = "dispatchKeypresses()V", at = @At(value = "HEAD"))
	public void updateF3(CallbackInfo callback) {
		boolean debugPressed = Keyboard.isKeyDown(61);
		if(this.debugPressed && !debugPressed) {
			if(cancelDebug) {
				cancelDebug = false;
			}
			else {
				gameSettings.showDebugInfo = !gameSettings.showDebugInfo;
			}
		}
		this.debugPressed = debugPressed;
	}

	private void debugChatInfo(String message) {
		ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("[" + EnumChatFormatting.GREEN + "Debug" + EnumChatFormatting.RESET + "] " + message));
	}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiNewChat;" +
			"clearChatMessages()V"))
	public void betterClearMessages(GuiNewChat guiNewChat) {
		guiNewChat.clearChatMessages();
		//((AccessGuiNewChat) guiNewChat).clearChatMessages();
		cancelDebug = true;
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/Minecraft;refreshResources()V"))
	public void betterRefreshResources(CallbackInfo callback) {
		debugChatInfo("Reloaded Resources");
		cancelDebug = true;
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;setOptionValue(Lv1_8_9/net/minecraft/client/settings/GameSettings$Options;I)V"))
	public void betterRenderDistance(CallbackInfo callback) {
		// Broken in vanilla.
		gameSettings.renderDistanceChunks = (int) Options.RENDER_DISTANCE.snapToStepClamp(gameSettings.renderDistanceChunks + (GuiScreen.isShiftKeyDown() ? -1 : 1));

		debugChatInfo("Render Distance: " + gameSettings.renderDistanceChunks);
		gameSettings.saveOptions();
		cancelDebug = true;
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer/RenderGlobal;loadRenderers()V"))
	public void betterLoadRenderers(CallbackInfo callback) {
		debugChatInfo("Reloaded Chunks");
		cancelDebug = true;
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE",
					target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;saveOptions()V"))
	public void advancedTootipsAndPauseOnLostFocus(CallbackInfo callback) {
		cancelDebug = true;
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE",
					target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;saveOptions()V", ordinal = 0))
	public void advancedTootips(CallbackInfo callback) {
		debugChatInfo("Advanced Item Tooltips: " + gameSettings.advancedItemTooltips);
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE",
					target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;saveOptions()V", ordinal = 1))
	public void pauseOnLostFocus(CallbackInfo callback) {
		debugChatInfo("Pause on Lost Focus: " + gameSettings.pauseOnLostFocus);
	}

	@Inject(method = "runTick()V",
			at = @At(value = "INVOKE",
					target = "Lv1_8_9/net/minecraft/client/renderer/entity/RenderManager;setDebugBoundingBox(Z)V"))
	public void betterHitboxes(CallbackInfo callback) {
		debugChatInfo("Entity Hitboxes: " + !renderManager.isDebugBoundingBox());
		cancelDebug = true;
	}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V", ordinal = 1))
	public void nextKey(int keyCode, boolean pressed) {
		if(pressed && debugPressed) {
			if(Keyboard.getEventKey() == 32
					|| Keyboard.getEventKey() == 31
					|| Keyboard.getEventKey() == 20
					|| Keyboard.getEventKey() == 33
					|| Keyboard.getEventKey() == 30
					|| Keyboard.getEventKey() == 35
					|| Keyboard.getEventKey() == 48
					|| Keyboard.getEventKey() == 25) {
				return;
			}
		}

		KeyBinding.setKeyBindState(keyCode, pressed);
	}

	// endregion

	private boolean hadWorld;

	@Inject(method = "loadWorld(Lv1_8_9/net/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
	public void onWorldLoad(WorldClient world, String loadingText, CallbackInfo callback) {
		if(world == null && hadWorld) {
			hadWorld = false;
		}
		else if(world != null && !hadWorld) {
			hadWorld = true;
			Client.INSTANCE.onServerChange(currentServerData);
		}
	}

	@Redirect(method = "startGame()V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/Minecraft;serverName:Ljava/lang/String;"))
	public String joinServer(Minecraft instance) {
		return null;
	}

	@Redirect(method = "startGame()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/Minecraft;" +
			"displayGuiScreen(Lv1_8_9/net/minecraft/client/gui/GuiScreen;)V", ordinal = 1))
	public void display(Minecraft instance, GuiScreen screen) {
		loading = false;

		if(serverName != null && serverName.startsWith("§sc§")) {
			ServerList list = new ServerList((Minecraft) (Object) this);
			instance.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(screen), (Minecraft) (Object) this,
					list.getServerData(Integer.parseInt(serverName.substring(4)))));
			return;
		}
		instance.displayGuiScreen(screen);
	}

	// Fix Replay Mod bug - textures animate too fast.

	private boolean earlyBird;

	//@Override
	//public void replayModSetEarlyReturnFromRunTick(boolean earlyBird) {
	//	this.earlyBird = earlyBird;
	//}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/renderer/texture/" +
			"TextureManager;tick()V"))
	public void cancelTextureTick(TextureManager instance) {
		if(!earlyBird) {
			instance.tick();
		}
	}

	@Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/gui/GuiIngame;" +
			"updateTick()V"))
	public void cancelHudTick(GuiIngame instance) {
		if(!earlyBird) {
			instance.updateTick();
		}
	}

	@Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I"))
	public void forceScroll(CallbackInfo callback) {
		if(earlyBird) {
			onScroll();
		}
	}

	@Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/client/Minecraft;" +
			"sendClickBlockToController(Z)V"), cancellable = true)
	public void doEarlyReturnFromRunTick(CallbackInfo callback) {
		if(earlyBird) {
			callback.cancel();
		}
	}

	@Inject(method = "shutdownMinecraftApplet()V" /* applet? looks like MCP is a bit outdated */, at = @At("HEAD"))
	public void preShutdown(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new GameQuitEvent());
	}

	@Shadow
	public GameSettings gameSettings;

	@Shadow
	private ServerData currentServerData;

	@Shadow
	public WorldClient theWorld;


	@Shadow
	public EntityRenderer entityRenderer;

	@Shadow
	public abstract void displayGuiScreen(GuiScreen guiScreenIn);

	@Shadow
	private String serverName;

	@Shadow
	public GuiIngame ingameGUI;

	@Shadow
	private RenderManager renderManager;

	@Shadow
	public PlayerControllerMP playerController;

}
