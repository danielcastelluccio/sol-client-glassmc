/*
 * Original code by mcblueparrot,
 * but was modified to be more similar to CreativeMD's original mod.
 */

package me.mcblueparrot.client.mod.impl;

import java.util.Map;
import java.util.WeakHashMap;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.ItemEntityRenderEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mixin.client.access.AccessEntity;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.renderer.GlStateManager;
import v1_8_9.net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import v1_8_9.net.minecraft.entity.item.EntityItem;
import v1_8_9.net.minecraft.item.Item;
import v1_8_9.net.minecraft.item.ItemStack;
import v1_8_9.net.minecraft.util.MathHelper;

public class ItemPhysicsMod extends Mod implements PrimaryIntegerSettingMod {

	@ConfigOption("Rotation Speed")
	@Slider(min = 0, max = 100, step = 1, suffix = "%")
	private float rotationSpeed = 100;
	private Map<EntityItem, ItemData> dataMap = new WeakHashMap<>(); // May cause a few small bugs, but memory
																	 // usage is prioritised.

	public ItemPhysicsMod() {
		super("Item Physics", "item_physics", "Add spinning animation to items.", ModCategory.VISUAL);
	}

	@EventHandler
	public void onItemEntityRenderEvent(ItemEntityRenderEvent event) {
		event.cancelled = true;

		ItemStack itemstack = event.entity.getEntityItem();
		Item item = itemstack.getItem();

		if(item != null) {
			boolean is3d = event.model.isGui3d();
			int clumpSize = getClumpSize(itemstack.stackSize);
			float f = 0.25F;
			float f1 =
					MathHelper.sin((event.entity.getAge() + event.partialTicks) / 10.0F
							+ event.entity.hoverStart) * 0.1F + 0.1F;
			float yScale =
					event.model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
			GlStateManager.translate((float) event.x, (float) event.y + 0.1, (float) event.z);

			float hover =
					((event.entity.getAge() + event.partialTicks) / 20.0F + event.entity.hoverStart)
							* (180F / (float)Math.PI);

			long now = System.nanoTime();

			ItemData data = dataMap.computeIfAbsent(event.entity, (itemStack) -> new ItemData(System.nanoTime()));

			long since = now - data.lastUpdate;

			GlStateManager.rotate(180, 0, 1, 1);
			GlStateManager.rotate(event.entity.rotationYaw, 0, 0, 1);

			if(!Minecraft.getMinecraft().isGamePaused()) {
				if(!event.entity.onGround) {
					int divisor = 2500000;
					if(((AccessEntity) event.entity).getIsInWeb()) {
						divisor *= 10;
					}
					data.rotation += ((float) since) / ((float) divisor) * (rotationSpeed / 100F);
				}
				else if(data.rotation != 0) {
					data.rotation = 0;
				}
			}

			GlStateManager.rotate(data.rotation, 0, 1, 0);

			data.lastUpdate = now;

			if(!is3d) {
				float rotationXAndY = -0.0F * (clumpSize - 1) * 0.5F;
				float rotationZ = -0.046875F * (clumpSize - 1) * 0.5F;
				GlStateManager.translate(rotationXAndY, rotationXAndY, rotationZ);
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			event.result = clumpSize;
		}
		else {
			event.result = 0;
		}
	}

	@Override
	public void decrement() {
		rotationSpeed = Math.max(0, rotationSpeed - 10);
	}

	@Override
	public void increment() {
		rotationSpeed = Math.min(100, rotationSpeed + 10);
	}

	private int getClumpSize(int size) {
		if(size > 48) {
			return 5;
		}
		else if(size > 32) {
			return 4;
		}
		else if(size > 16) {
			return 3;
		}
		else if(size > 1) {
			return 2;
		}
		return 1;
	}

	public static class ItemData {

		public long lastUpdate;
		public float rotation;

		public ItemData(long lastUpdate) {
			this.lastUpdate = lastUpdate;
		}

	}

}
