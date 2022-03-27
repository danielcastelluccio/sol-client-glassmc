package me.mcblueparrot.client.mixin.mod;

import me.mcblueparrot.client.mod.impl.hud.chat.ChatAnimationData;
import me.mcblueparrot.client.mod.impl.hud.chat.ChatMod;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.*;
import v1_8_9.net.minecraft.client.resources.I18n;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import lombok.Getter;
import lombok.Setter;
import v1_8_9.net.minecraft.client.settings.GameSettings;

public class MixinChatMod {

	@Mixin(GuiScreen.class)
	public static class MixinGuiScreen {

		@Redirect(method = "handleComponentClick(Lv1_8_9/net/minecraft/util/IChatComponent;)Z",
				at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;chatLinks:Z"))
		public boolean overrideChatLinks(GameSettings settings) {
			if(ChatMod.enabled) {
				return ChatMod.instance.links;
			}

			return settings.chatLinks;
		}

		@Redirect(method = "handleComponentClick(Lv1_8_9/net/minecraft/util/IChatComponent;)Z",
				at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;chatLinksPrompt:Z"))
		public boolean overrideChatLinksPrompt(GameSettings settings) {
			if(ChatMod.enabled) {
				return ChatMod.instance.promptLinks;
			}

			return settings.chatLinks;
		}

	}

	@Mixin(GameSettings.class)
	public static class MixinGameSettings {

		@Redirect(method = "sendSettingsToServer()V",
				at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;chatColours:Z"))
		public boolean overrideChatColours(GameSettings settings) {
			if(ChatMod.enabled) {
				return ChatMod.instance.colours;
			}

			return settings.chatColours;
		}

	}

	@Mixin(GuiUtilRenderComponents.class)
	public static class MixinGuiUtilRenderComponents {

		@Inject(method = "func_178909_a(Ljava/lang/String;Z)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
		private static void overrideChatColours(String input, boolean defaultValue,
										   CallbackInfoReturnable<String> callback) {
			if(ChatMod.enabled) {
				callback.setReturnValue(input);
			}
		}

	}

	@Mixin(GuiChat.class)
	public static class MixinGuiChat {

		public boolean canBeForceClosed() {
			if(ChatMod.enabled) {
				return !ChatMod.instance.preventClose;
			}
			return true;
		}

	}

	@Mixin(GuiNewChat.class)
	public static class MixinGuiNewChat {

		@Inject(method = "getChatOpen()Z", at = @At("HEAD"), cancellable = true)
		public void overrideChatOpen(CallbackInfoReturnable<Boolean> callback) {
			if(ChatMod.enabled && ChatMod.instance.peekKey.isKeyDown()) {
				callback.setReturnValue(true);
			}
		}

		@Redirect(method = "getChatWidth()I", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;chatWidth"))
		public float overrideChatWidth(GameSettings instance) {
			if(ChatMod.enabled) {
				return ChatMod.instance.width / 320F;
			}

			return instance.chatWidth;
		}

		@Redirect(method = "getChatHeight()I", at = @At(value = "FIELD", target =
				"Lv1_8_9/net/minecraft/client/settings/GameSettings;chatHeightFocused"))
		public float overrideOpenChatHeight(GameSettings instance) {
			if(ChatMod.enabled) {
				return ChatMod.instance.openHeight / 180F;
			}

			return instance.chatHeightFocused;
		}

		@Redirect(method = "getChatHeight()I", at = @At(value = "FIELD", target =
				"Lv1_8_9/net/minecraft/client/settings/GameSettings;chatHeightUnfocused"))
		public float overrideClosedChatHeight(GameSettings instance) {
			if(ChatMod.enabled) {
				return ChatMod.instance.closedHeight / 180F;
			}

			return instance.chatHeightFocused;
		}

		@Redirect(method = "getChatScale()F", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;chatScale"))
		public float overrideChatScale(GameSettings instance) {
			if(ChatMod.enabled) {
				return ChatMod.instance.scale / 100F;
			}

			return instance.chatScale;
		}

	}

	@Mixin(Minecraft.class)
	public static class MixinMinecraft {

		@Redirect(method = "runTick()V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/settings/GameSettings;" +
				"chatVisibility"))
		public EntityPlayer.EnumChatVisibility overrideChatVisibility(GameSettings instance) {
			if(ChatMod.enabled) {
				return EntityPlayer.EnumChatVisibility.FULL; /* Always allow chat to be opened */
			}

			return instance.chatVisibility;
		}

	}

	@Mixin(ScreenChatOptions.class)
	public static class MixinScreenChatOptions extends GuiScreen {

		@Shadow @Final
		private GameSettings game_settings;

		@Inject(method = "initGui()V", at = @At(value = "FIELD", target = "Lv1_8_9/net/minecraft/client/gui/ScreenChatOptions;" +
				"field_146401_i", shift = At.Shift.AFTER), cancellable = true)
		public void replaceGui(CallbackInfo callback) {
			if(ChatMod.enabled) {
				buttonList.add(new GuiOptionButton(GameSettings.Options.REDUCED_DEBUG_INFO.returnEnumOrdinal(),
						this.width / 2 - (150 / 2), this.height / 6 + 76,
						GameSettings.Options.REDUCED_DEBUG_INFO, game_settings.getKeyBinding(GameSettings.Options.REDUCED_DEBUG_INFO)));
				buttonList.add(new GuiButton(201, this.width / 2 - (150 / 2),
						this.height / 6 + 98, 150, 20, "More Options..."));
				buttonList.add(new GuiButton(200, this.width / 2 - (150 / 2),
						this.height / 6 + 120, 150, 20, I18n.format("gui.done")));
				callback.cancel();
			}
		}

		@Inject(method = "actionPerformed(Lv1_8_9/net/minecraft/client/gui/GuiButton;)V", at = @At("RETURN"))
		public void actionPerformed(GuiButton button, CallbackInfo callback) {
			if(button.id == 201) {
				mc.displayGuiScreen(new ModsScreen(ChatMod.instance));
			}
		}

	}

	@Mixin(ChatLine.class)
	public static class MixinChatLine implements ChatAnimationData {

		@Getter
		@Setter
		private float transparency = 1;

		@Getter
		@Setter
		private float lastTransparency = 1;

	}

}
