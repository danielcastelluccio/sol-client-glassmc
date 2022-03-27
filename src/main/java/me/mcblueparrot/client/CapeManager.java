package me.mcblueparrot.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import v1_8_9.com.google.gson.JsonElement;
import v1_8_9.com.google.gson.JsonObject;
import v1_8_9.com.google.gson.JsonParser;
import v1_8_9.org.apache.logging.log4j.LogManager;
import v1_8_9.org.apache.logging.log4j.Logger;

import v1_8_9.com.mojang.authlib.minecraft.MinecraftProfileTexture;

import me.mcblueparrot.client.util.Utils;
import v1_8_9.net.minecraft.client.Minecraft;
import v1_8_9.net.minecraft.client.renderer.ThreadDownloadImageData;
import v1_8_9.net.minecraft.entity.player.EntityPlayer;
import v1_8_9.net.minecraft.util.ResourceLocation;

public class CapeManager {

	private static final Logger LOGGER = LogManager.getLogger();
	private Map<String, String> capes = new HashMap<>();
	private Map<UUID, ResourceLocation> capeCache = new HashMap<>();
	private static final String BASE_URL = "https://raw.githubusercontent.com/Sol-Client/Capes/main/";
	private static final URL BY_PLAYER_URL = Utils.sneakyParse(BASE_URL + "by_player.json");

	public CapeManager() throws IOException {
		JsonObject capesObject = new JsonParser().parse(new InputStreamReader(BY_PLAYER_URL.openStream()))
				.getAsJsonObject();

		for(Map.Entry<String, JsonElement> entry : capesObject.entrySet()) {
			capes.put(entry.getKey(), BASE_URL + "capes/" + entry.getValue().getAsString() + ".png");
		}
	}

	public ResourceLocation getForPlayer(EntityPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();

		String capeUrl = capes.get(player.getUniqueID().toString().replace("-", ""));

		if(capeUrl == null) {
			return null;
		}

		try {
			if(capeCache.containsKey(player.getUniqueID())) {
				return capeCache.get(player.getUniqueID());
			}
			else {
				MinecraftProfileTexture texture = new MinecraftProfileTexture(capeUrl, null);

				ResourceLocation cape = new ResourceLocation("sc_capes/" + texture.getHash());

				if(mc.getTextureManager().getTexture(cape) == null) {
					ThreadDownloadImageData thread = new ThreadDownloadImageData(null, texture.getUrl(), null, null);
					mc.getTextureManager().loadTexture(cape, thread);
				}

				capeCache.put(player.getUniqueID(), cape);

				return cape;
			}
		}
		catch(Exception error) {
			LOGGER.error("Could not download cape", error);
		}

		return null;
	}

}
