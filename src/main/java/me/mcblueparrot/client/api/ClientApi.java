package me.mcblueparrot.client.api;

import v1_8_9.org.apache.logging.log4j.LogManager;
import v1_8_9.org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.mod.Mod;
import v1_8_9.net.minecraft.network.play.server.S3FPacketCustomPayload;

public class ClientApi {

	private static final Logger LOGGER = LogManager.getLogger();

	@EventHandler
	public void onCustomPayload(S3FPacketCustomPayload payload) {
		if(payload.getChannelName().startsWith("solclient:")) {
			String message = payload.getBufferData().readStringFromBuffer(32767);

			if(payload.getChannelName().equals("solclient:block_mods")) {
				JsonArray array = new JsonParser().parse(message).getAsJsonArray();

				Client.INSTANCE.getMods().forEach(Mod::unblock);

				for(JsonElement element : array) {
					String modId = element.getAsString();

					Mod mod = Client.INSTANCE.getModById(modId);

					if(mod != null) {
						mod.block();
					}
					else {
						LOGGER.warn("Cannot block mod " + modId + ": not found");
					}
				}
			}
			else if(payload.getChannelName().equals("solclient:popup")) {
				JsonObject data = new JsonParser().parse(message).getAsJsonObject();

				String text = data.get("text").getAsString();
				String command = data.get("command").getAsString();

				Client.INSTANCE.getPopupManager().add(new Popup(text, command));
			}
		}
	}

}
