package me.mcblueparrot.client.event.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import v1_8_9.net.minecraft.client.shader.ShaderGroup;

@RequiredArgsConstructor
public class PostProcessingEvent {

	public final Type type;
	public List<ShaderGroup> groups = new ArrayList<>();

	public enum Type {
		RENDER,
		UPDATE
	}

}
