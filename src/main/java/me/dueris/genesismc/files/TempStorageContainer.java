package me.dueris.genesismc.files;

import me.dueris.genesismc.GenesisMC;
import net.minecraft.server.commands.LocateCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TempStorageContainer {

    private static File tempFolder;

    public static class Storage {
        private final File folder;
        private final File file;
        private final FileConfiguration config;

        public Storage(String subfolderName, String fileName) {
            tempFolder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "temp");
            if (!tempFolder.exists()) {
                tempFolder.mkdirs();
            }
            folder = new File(tempFolder, subfolderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            file = new File(folder, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            config = YamlConfiguration.loadConfiguration(file);
        }

        public FileConfiguration getConfig() {
            return config;
        }

        public boolean saveConfig() {
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        public boolean setValue(String path, Object value) {
            config.set(path, value);
            saveConfig();
            return true;
        }

        public Object getValue(String path) {
            return config.get(path);
        }

        public boolean deleteValue(String path) {
            config.set(path, null);
            saveConfig();
            return true;
        }
    }

    public static class BiomeStorage extends Storage {
        public BiomeStorage() {
            super("biome", "craftData.yml");
        }

        private ExecutorService executorService;

        public boolean addValues() {
//            executorService = Executors.newFixedThreadPool(8);
//            for (World world : Bukkit.getWorlds()) {
//                for (Biome biome : Biome.values()) {
//                    if (super.getValue(biome.name().toLowerCase()) == null || super.getValue(biome.name().toLowerCase()) == "not_found") {
//                        executorService.submit(() -> {
//                            Location location = world.locateNearestBiome(new Location(world, 0, 0, 0), biome, 200);
//                            String result = (location != null) ? location.toString() : "not_found";
//                            super.setValue(biome.name().toLowerCase(), result);
//                        });
//                    }
//                }
//            }
//            executorService.shutdown();
            return true;
        }
    }

    public static class StructureStorage extends Storage {
        public StructureStorage() {
            super("structure", "craftData.yml");
        }

        private ExecutorService executorService;

        public boolean addValues(){
//            executorService = Executors.newFixedThreadPool(8);
//            for (World world : Bukkit.getWorlds()) {
//                for (StructureType biome : StructureType.getStructureTypes().values()) {
//                    if (super.getValue(biome.getName().toLowerCase()) == null || super.getValue(biome.getName().toLowerCase()) == "not_found") {
//                        if(super.getValue(biome.getName().toLowerCase()) == "not_found") continue;
//                        executorService.submit(() -> {
//                            Location location = world.locateNearestStructure(new Location(world, 0, 0, 0), biome, 200, true);
//                            String result = (location != null) ? location.toString() : "not_found";
//                            super.setValue(biome.getName().toLowerCase(), result);
//                        });
//                    }
//                }
//            }
//            executorService.shutdown();
            return true;
        }
    }

}
