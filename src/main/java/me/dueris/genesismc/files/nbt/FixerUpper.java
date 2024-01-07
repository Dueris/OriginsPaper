package me.dueris.genesismc.files.nbt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Stopwatch;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;

public class FixerUpper {
    public static void fixupFile(File f) throws IOException{
        if (f.exists()) {
            CompoundTag playerData = NbtIo.readCompressed(f.toPath(), NbtAccounter.unlimitedHeap());
            if(playerData.contains("BukkitValues")){
                CompoundTag bukkitVals = playerData.getCompound("BukkitValues");
                if(bukkitVals.contains("genesismc:originlayer")){
                    // Fixes issue with origin data being null, causing extreme issues in the plugin
                    if(bukkitVals.getString("genesismc:originlayer") == null || bukkitVals.getString("genesismc:originlayer") == ""){
                        HashMap<LayerContainer, OriginContainer> origins = new HashMap<>();
                        for (LayerContainer layer : CraftApoli.getLayers()) origins.put(layer, CraftApoli.nullOrigin());
                        bukkitVals.putString("genesismc:originlayer", CraftApoli.toOriginSetSaveFormat(origins));
                    }
                }
            }
            NbtIo.writeCompressed(playerData, f.toPath());
        }
    }

    public static void runFixerUpper() throws IOException{
        Stopwatch stopwatch = Stopwatch.createStarted();
        int[] i = {0};
        for(File f : MinecraftServer.getServer().playerDataStorage.getPlayerDir().listFiles()){
            GenesisMC.loaderThreadPool.submit(() -> {
                try {
                    fixupFile(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i[0]++;
            });
        }
        while(true){
            if(i[0] == MinecraftServer.getServer().playerDataStorage.getPlayerDir().listFiles().length){
                System.out.println("FixerUpper took {time} ms and completed successfully.".replace("{time}", String.valueOf(stopwatch.stop().elapsed().toMillis())));
                break;
            }
        }
    }
}