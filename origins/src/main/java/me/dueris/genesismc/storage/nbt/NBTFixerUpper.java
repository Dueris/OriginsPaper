package me.dueris.genesismc.storage.nbt;

import com.google.common.base.Stopwatch;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
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
                if (bukkitVals.contains("genesismc:originlayer")) {
                    // Fixes issue with origin data being null, causing extreme issues in the plugin
                    if (bukkitVals.getString("genesismc:originlayer") == null || bukkitVals.getString("genesismc:originlayer") == "") {
                        HashMap<Layer, Origin> origins = new HashMap<>();
                        for (Layer layer : CraftApoli.getLayersFromRegistry())
                            origins.put(layer, CraftApoli.nullOrigin());
                        bukkitVals.putString("genesismc:originlayer", CraftApoli.toOriginSetSaveFormat(origins));
                    }
                }
            }
            NbtIo.writeCompressed(playerData, f.toPath());
        }
    }

    public static void runFixerUpper() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        File[] filesToFix = GenesisMC.server.playerDataStorage.getPlayerDir().listFiles();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        System.out.println("Found (x) files in (dir)"
            .replace("(x)", String.valueOf(filesToFix.length))
            .replace("(dir)", GenesisMC.server.playerDataStorage.getPlayerDir().toPath().toString()));

        for (File f : filesToFix) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    if (!f.getPath().endsWith(".dat_old")) {
                        fixupFile(f);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, GenesisMC.loaderThreadPool);

            futures.add(future);
        }

        // Wait for all files to complete FixerUpper
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTasks.join();
        System.out.println("FixerUpper took {time} ms and completed successfully.".replace("{time}", String.valueOf(stopwatch.stop().elapsed().toMillis())));
    }
}