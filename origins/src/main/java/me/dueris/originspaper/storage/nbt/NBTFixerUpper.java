package me.dueris.originspaper.storage.nbt;

import com.google.common.base.Stopwatch;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NBTFixerUpper {
	public static void fixupFile(File f) throws IOException {
		if (f.exists()) {
			CompoundTag playerData = NbtIo.readCompressed(f.toPath(), NbtAccounter.unlimitedHeap());
			if (playerData.contains("BukkitValues")) {
				CompoundTag bukkitVals = playerData.getCompound("BukkitValues");
				if (bukkitVals.contains("originspaper:originlayer")) {
					// Fixes issue with origin data being null, causing extreme issues in the plugin
					if (bukkitVals.getString("originspaper:originlayer") == null || bukkitVals.getString("originspaper:originlayer") == "") {
						HashMap<Layer, Origin> origins = new HashMap<>();
						for (Layer layer : CraftApoli.getLayersFromRegistry())
							origins.put(layer, CraftApoli.emptyOrigin());
						bukkitVals.putString("originspaper:originlayer", CraftApoli.toOriginSetSaveFormat(origins));
					}
				}
			}
			NbtIo.writeCompressed(playerData, f.toPath());
		}
	}

	public static void runFixerUpper() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		File[] filesToFix = OriginsPaper.server.playerDataStorage.getPlayerDir().listFiles();
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		System.out.println("Found (x) files in (dir)"
			.replace("(x)", String.valueOf(filesToFix.length))
			.replace("(dir)", OriginsPaper.server.playerDataStorage.getPlayerDir().toPath().toString()));

		for (File f : filesToFix) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					if (!f.getPath().endsWith(".dat_old")) {
						fixupFile(f);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, OriginsPaper.loaderThreadPool);

			futures.add(future);
		}

		// Wait for all files to complete FixerUpper
		CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		allTasks.join();
		System.out.println("FixerUpper took {time} ms and completed successfully.".replace("{time}", String.valueOf(stopwatch.stop().elapsed().toMillis())));
	}
}