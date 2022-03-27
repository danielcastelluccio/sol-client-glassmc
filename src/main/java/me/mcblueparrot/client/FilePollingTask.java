package me.mcblueparrot.client;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.mcblueparrot.client.mod.CachedConfigOption;
import me.mcblueparrot.client.mod.Mod;
import v1_8_9.net.minecraft.client.Minecraft;

public class FilePollingTask implements Runnable, Closeable {

	private Map<String, CachedConfigOption> files = new HashMap<>();
	private WatchKey key;

	public FilePollingTask(List<Mod> mods) throws IOException {
		WatchService service = FileSystems.getDefault().newWatchService();

		key = Minecraft.getMinecraft().mcDataDir.toPath().register(service, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

		for(Mod mod : mods) {
			for(CachedConfigOption option : mod.getOptions()) {
				if(option.file != null) {
					files.put(option.file.getName(), option);
				}
			}
		}
	}

	@Override
	public void run() {
		for(WatchEvent<?> event : key.pollEvents()) {
			File changedFile = ((Path) event.context()).toFile();

			CachedConfigOption option = files.get(changedFile.getName());

			if(option != null) {
				try {
					option.readFile();
				}
				catch(IOException error) {
				}
			}
		}
	}

	@Override
	public void close() {
		key.reset();
	}

}
