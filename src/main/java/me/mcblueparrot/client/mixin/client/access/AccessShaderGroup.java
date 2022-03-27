package me.mcblueparrot.client.mixin.client.access;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import v1_8_9.net.minecraft.client.shader.Shader;
import v1_8_9.net.minecraft.client.shader.ShaderGroup;

@Mixin(ShaderGroup.class)
public interface AccessShaderGroup {

	@Accessor
	List<Shader> getListShaders();

}
