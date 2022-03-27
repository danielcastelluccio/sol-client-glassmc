package me.mcblueparrot.client.mixin.mod;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.mod.impl.TweaksMod;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.gui.FontRenderer;
import v1_8_9.net.minecraft.client.gui.GuiButton;
import v1_8_9.net.minecraft.client.gui.GuiIngame;
import v1_8_9.net.minecraft.client.gui.GuiIngameMenu;
import v1_8_9.net.minecraft.client.gui.ScaledResolution;
import v1_8_9.net.minecraft.client.renderer.EntityRenderer;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.client.renderer.InventoryEffectRenderer;
import v1_8_9.net.minecraft.client.renderer.entity.RenderManager;
import v1_8_9.net.minecraft.client.renderer.entity.RendererLivingEntity;
import v1_8_9.net.minecraft.client.resources.I18n;
import v1_8_9.net.minecraft.client.settings.GameSettings;
import v1_8_9.net.minecraft.client.settings.KeyBinding;
import v1_8_9.net.minecraft.enchantment.Enchantment;
import v1_8_9.net.minecraft.entity.Entity;
import v1_8_9.net.minecraft.item.ItemPotion;
import v1_8_9.net.minecraft.item.ItemStack;
import v1_8_9.net.minecraft.util.EnumChatFormatting;
import v1_8_9.net.minecraft.util.StatCollector;

public class MixinTweaksMod {

	@Mixin(GuiIngame.class)
	public static abstract class MixinGuiIngame {

		@Inject(method = "renderSelectedItem(Lv1_8_9/net/minecraft/client/gui/ScaledResolution;)V", at = @At("HEAD"), cancellable = true)
		public void drawExtraLines(ScaledResolution scaledRes, CallbackInfo callback) {
			if(TweaksMod.enabled && TweaksMod.instance.betterTooltips) {
				callback.cancel();

				mc.mcProfiler.startSection("selectedItemName");

				if(remainingHighlightTicks > 0 && highlightingItemStack != null) {
					List<String> lines = highlightingItemStack.getTooltip(mc.thePlayer, false);

					int y = scaledRes.getScaledHeight() - 59;

					int height = getFontRenderer().FONT_HEIGHT + 2;

					y -= (height * (lines.size() - 1)) - 2;

					if(!this.mc.playerController.shouldDrawHUD()) {
						y += 14;
					}

					int opacity = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
					opacity = Math.min(opacity, 255);

					if(opacity > 0) {
						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
						for(String line : lines) {
							int x = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(line)) / 2;
							getFontRenderer().drawStringWithShadow(line, x, y,
									16777215 + (opacity << 24));
							y += height;
						}
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				}

				mc.mcProfiler.endSection();
			}
		}

		@Shadow
		@Final
		private Minecraft mc;

		@Shadow
		private int remainingHighlightTicks;

		@Shadow
		private ItemStack highlightingItemStack;

		@Shadow
		public abstract FontRenderer getFontRenderer();


	}

	@Mixin(Enchantment.class)
	public static abstract class MixinEnchantment {

		@Inject(method = "getTranslatedName(I)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
		public void overrideName(int level, CallbackInfoReturnable<String> callback) {
			System.out.println("test1");
			if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals) {
				callback.setReturnValue(StatCollector.translateToLocal(getName()) + " " + level);
			}
		}

		@Shadow
		public abstract String getName();

	}

	@Mixin(InventoryEffectRenderer.class)
	public static class MixinInventoryEffectRenderer {

		@Redirect(method = "drawActivePotionEffects()V", at = @At(value = "INVOKE",
				target = "Lv1_8_9/net/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
		public String overrideLevel(String translateKey, Object[] parameters) {
			if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals && translateKey.startsWith("enchantment.level.")) {
				return Integer.toString(Integer.parseInt(translateKey.substring(18)));
			}

			return I18n.format(translateKey, parameters);
		}

	}

	@Mixin(ItemPotion.class)
	public static class MixinItemPotion {

		@Redirect(method = "addInformation(Lv1_8_9/net/minecraft/item/ItemStack;Lv1_8_9/net/minecraft/entity/player/EntityPlayer;Ljava/util/List;Z)V", at = @At(value = "INVOKE",
				target = "Lv1_8_9/net/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
				ordinal = 1))
		public String overrideAmplifier(String key) {
			if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals && key.startsWith("potion.potency.")) {
				return Integer.toString(Integer.parseInt(key.substring(15)) + 1);
			}
			return StatCollector.translateToLocal(key);
		}

	}

	@Mixin(RendererLivingEntity.class)
	public static class MixinRendererLivingEntity {

		@Redirect(method = "canRenderName(Lv1_8_9/net/minecraft/entity/EntityLivingBase;)Z", at = @At(value = "FIELD",
				target = "Lv1_8_9/net/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lv1_8_9/net/minecraft/entity/Entity;"))
		public Entity renderOwnName(RenderManager manager) {
			if(TweaksMod.enabled && TweaksMod.instance.showOwnTag) {
				return null;
			}
			return manager.livingPlayer;
		}

	}

	@Mixin(EntityRenderer.class)
	public static abstract class MixinEntityRenderer {

		@Redirect(method = "setupCameraTransform(FI)V", at = @At(value = "INVOKE",
				target = "Lv1_8_9/net/minecraft/client/renderer/EntityRenderer;setupViewBobbing(F)V"))
		public void cancelWorldBobbing(EntityRenderer instance, float partialTicks) {
			if(TweaksMod.enabled && TweaksMod.instance.minimalViewBobbing) {
				return;
			}

			setupViewBobbing(partialTicks);
		}

		@Shadow
		protected abstract void setupViewBobbing(float partialTicks);

	}

	@Mixin(GuiIngameMenu.class)
	public static class MixinGuiIngameMenu {

		private boolean disconnect;

		@Inject(method = "initGui()V", at = @At("HEAD"))
		public void initGui(CallbackInfo callback) {
			disconnect = !isConfirmEnabled();
		}

		@Inject(method = "actionPerformed(Lv1_8_9/net/minecraft/client/gui/GuiButton;)V", at = @At("HEAD"), cancellable = true)
		public void overrideButton(GuiButton button, CallbackInfo callback) {
			if(button.id == 1 && !disconnect) {
				callback.cancel();
				button.displayString = EnumChatFormatting.GREEN + "Press Again to Confirm";
				disconnect = true;
			}
		}

		private boolean isConfirmEnabled() {
			return TweaksMod.enabled && TweaksMod.instance.confirmDisconnect;
		}

	}

	@Mixin(Minecraft.class)
	public static class MixinMinecraft {

		@Inject(method = "setIngameFocus()V", at = @At(value = "INVOKE", target = "Lv1_8_9/net/minecraft/util/MouseHelper;grabMouseCursor()V"))
		public void afterLock(CallbackInfo callback) {
			if(TweaksMod.enabled && TweaksMod.instance.betterKeyBindings) {
				for(KeyBinding keyBinding : gameSettings.keyBindings) {
					try {
						KeyBinding.setKeyBindState(keyBinding.getKeyCode(), keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
					}
					catch (IndexOutOfBoundsException error) {
					}
				}
			}
		}

		@Shadow
		public GameSettings gameSettings;

	}

}
