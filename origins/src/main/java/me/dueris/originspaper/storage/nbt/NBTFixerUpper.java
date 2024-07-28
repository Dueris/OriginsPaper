package me.dueris.originspaper.storage.nbt;

import com.google.common.base.Stopwatch;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.registry.registries.OriginLayer;
import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NBTFixerUpper {
	public static void fixupFile(@NotNull File f) throws IOException {
		if (f.exists()) {
			CompoundTag playerData = NbtIo.readCompressed(f.toPath(), NbtAccounter.unlimitedHeap());
			if (playerData.contains("BukkitValues")) {
				CompoundTag bukkitVals = playerData.getCompound("BukkitValues");
				if (bukkitVals.contains("originspaper:originlayer")
					&& (bukkitVals.getString("originspaper:originlayer") == null || bukkitVals.getString("originspaper:originlayer").equals(""))) {
					HashMap<OriginLayer, Origin> origins = new HashMap<>();

					for (OriginLayer layer : CraftApoli.getLayersFromRegistry()) {
						origins.put(layer, CraftApoli.emptyOrigin());
					}

					bukkitVals.putString("originspaper:originlayer", CraftApoli.toOriginSetSaveFormat(origins));
				}
			}

			NbtIo.writeCompressed(playerData, f.toPath());
		}
	}

	public static void runFixerUpper() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		File[] filesToFix = OriginsPaper.server.playerDataStorage.getPlayerDir().listFiles();
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		OriginsPaper.getPlugin()
			.debug(
				Component.text(
					"Found (x) files in (dir)"
						.replace("(x)", String.valueOf(filesToFix.length))
						.replace("(dir)", OriginsPaper.server.playerDataStorage.getPlayerDir().toPath().toString())
				)
			);

		for (File f : filesToFix) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					if (!f.getPath().endsWith(".dat_old")) {
						fixupFile(f);
					}
				} catch (IOException var2x) {
					var2x.printStackTrace();
				}
			}, OriginsPaper.loaderThreadPool);
			futures.add(future);
		}

		CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		allTasks.join();
		OriginsPaper.getPlugin()
			.debug(
				Component.text("FixerUpper took {time} ms and completed successfully.".replace("{time}", String.valueOf(stopwatch.stop().elapsed().toMillis())))
			);
	}
}
