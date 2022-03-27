package me.mcblueparrot.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import lombok.Getter;
import lombok.Setter;
import me.mcblueparrot.client.culling.Cullable;
import v1_8_9.net.minecraft.tileentity.TileEntity;

@Mixin(TileEntity.class)
public class MixinTileEntity implements Cullable {

	@Getter
	@Setter
	private boolean culled;

}
