package me.dueris.genesismc.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public class GenesisConfigs {

    public static FileConfiguration mainConfig;
    public static File mainConfigFile;
    public static FileConfiguration orbconfig;
    public static File orbConfigFile;

    public static void setup() {
        File lang = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "lang");
        File skins = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "skins");
        orbConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "orboforigins.yml");
        mainConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origin-server.yml");

        new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "english.yml").delete();
        new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "german.yml").delete();
        new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "russian.yml").delete();
        new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "trad-chinese.yml").delete();

        if (!lang.exists()) {
            lang.mkdirs();
        }
        if (!skins.exists()) {
            skins.mkdirs();
        }

        if (!mainConfigFile.exists()) {
            try {
                mainConfigFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("There was an eror creating %fileName%".replace("%fileName%", "origin-server.yml"));
            }
        }

        if (!orbConfigFile.exists()) {
            try {
                orbConfigFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("There was an eror creating %fileName%".replace("%fileName%", "orboforigins.yml"));
            }
        }

    }

    private static void addMissingLines(File configFile, String filePath) {
        try {
            File tempConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "tempfile.yml");
            InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource(filePath);
            Files.copy(inputStream, tempConfigFile.toPath());

            FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(tempConfigFile);
            FileConfiguration sourceConfig = YamlConfiguration.loadConfiguration(configFile);
            Set<String> tempKeys = tempConfig.getKeys(true);
            Set<String> sourceKeys = sourceConfig.getKeys(true);

            if (sourceKeys.equals(tempKeys)) {
                tempConfigFile.delete();
                return;
            }
            for (String key : sourceKeys) tempKeys.remove(key);
            for (String key : tempKeys) sourceConfig.set(key, tempConfig.get(key));

            sourceConfig.save(configFile);
            tempConfigFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadMainConfig() {
        mainConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origin-server.yml");
        if (!mainConfigFile.exists()) {
            mainConfigFile.getParentFile().mkdirs();
            try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("origin-server.yml")) {
                Files.copy(inputStream, mainConfigFile.toPath());
            } catch (Exception e) {
                getLogger().severe("There was an eror creating %fileName%".replace("%fileName%", "origin-server.yml"));
            }
        }

        // Load the custom configuration file
        addMissingLines(mainConfigFile, "origin-server.yml");
        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);
    }

    public static void loadOrbConfig() {
        orbConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "orboforigins.yml");
        if (!orbConfigFile.exists()) {
            orbConfigFile.getParentFile().mkdirs();
            try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("orboforigins.yml")) {
                Files.copy(inputStream, orbConfigFile.toPath());
            } catch (Exception e) {
                getLogger().severe("There was an eror creating %fileName%".replace("%fileName%", "orboforigins.yml"));
            }
        }

        // Load the custom configuration file
        addMissingLines(orbConfigFile, "orboforigins.yml");
        orbconfig = YamlConfiguration.loadConfiguration(orbConfigFile);
    }

    public static File getFile(String T) {
        File langFolder = new File(Bukkit.getPluginManager().getPlugin("genesismc").getDataFolder(), "lang");
        File langFile = new File(langFolder, T + ".yml");

        if (!langFile.exists()) {
            return null;
        }

        return langFile;
    }

    public static FileConfiguration getMainConfig() {
        return mainConfig;
    }

    public static FileConfiguration getOrbCon() {
        return orbconfig;
    }

}
