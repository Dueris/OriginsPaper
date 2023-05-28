package me.dueris.genesismc.core.files;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;

import static org.bukkit.Bukkit.getLogger;

public class GenesisDataFiles {

  public static FileConfiguration mainConfig;
  public static File mainConfigFile;
  public static FileConfiguration orbconfig;
  public static File orbConfigFile;
  public static File lang;
  public static FileConfiguration englishLang;
  public static File englishLangFile;
  public static FileConfiguration germanLang;
  public static File germanLangFile;
  public static FileConfiguration russianLang;
  public static File russianLangFile;

  public static void setup() {
    File custom_folder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
    File lang = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "lang");
    orbConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "orboforigins.yml");
    mainConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origin-server.yml");
    englishLangFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "english-lang.yml");
    germanLangFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "german-lang.yml");
    russianLangFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "lang", "russian-lang.yml");
    if (!custom_folder.exists()) {
      custom_folder.mkdirs();
    }
    if (!lang.exists()) {
      lang.mkdirs();
    }


    if (!mainConfigFile.exists()) {
      try {
        mainConfigFile.createNewFile();
      } catch (IOException var3) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create origin-server.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if (!orbConfigFile.exists()) {
      try {
        orbConfigFile.createNewFile();
      } catch (IOException var2) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create orboforigins.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if (!englishLangFile.exists()) {
      try {
        englishLangFile.createNewFile();
      } catch (IOException var2) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create english-lang.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if (!germanLangFile.exists()) {
      try {
        germanLangFile.createNewFile();
      } catch (IOException var2) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create german-lang.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if (!russianLangFile.exists()) {
      try {
        russianLangFile.createNewFile();
      } catch (IOException var2) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create russian-lang.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

  }

  public static void save() {
    try {
      mainConfig.save(mainConfigFile);
    } catch (IOException var2) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file: " + var2.getMessage());
    }

    try {
      orbconfig.save(orbConfigFile);
    } catch (IOException var1) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file: " + var1.getMessage());
    }

    try {
      englishLang.save(englishLangFile);
    } catch (IOException e) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file: " + e.getMessage());
    }

    try {
      russianLang.save(russianLangFile);
    } catch (IOException e) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file: " + e.getMessage());
    }

    try {
      germanLang.save(germanLangFile);
    } catch (IOException e) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file: " + e.getMessage());
    }

  }


  public static void loadMainConfig(){
    mainConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origin-server.yml");
    if (!mainConfigFile.exists()) {
      mainConfigFile.getParentFile().mkdirs();
      try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("origin-server.yml")) {
        Files.copy(inputStream, mainConfigFile.toPath());
      } catch (Exception e) {
        getLogger().severe("Failed to create custom configuration file: " + e.getMessage());
      }
    }

    // Load the custom configuration file
    mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);
    }

  public static void loadOrbConfig(){
    orbConfigFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "orboforigins.yml");
    if (!orbConfigFile.exists()) {
      orbConfigFile.getParentFile().mkdirs();
      try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("orboforigins.yml")) {
        Files.copy(inputStream, orbConfigFile.toPath());
      } catch (Exception e) {
        getLogger().severe("Failed to create custom configuration file: " + e.getMessage());
      }
    }

    // Load the custom configuration file
    orbconfig = YamlConfiguration.loadConfiguration(orbConfigFile);
  }

  public static void loadLangConfig(){
    englishLangFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "/lang/" + File.separator , "english-lang.yml");
    if (!englishLangFile.exists()) {
      englishLangFile.getParentFile().mkdirs();
      try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("english-lang.yml")) {
        Files.copy(inputStream, englishLangFile.toPath());
      } catch (Exception e) {
        getLogger().severe("Failed to create custom configuration file: " + e.getMessage());
      }
    }

    // Load the custom configuration file
    englishLang = YamlConfiguration.loadConfiguration(englishLangFile);

    russianLangFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "/lang/" + File.separator , "russian-lang.yml");
    if (!russianLangFile.exists()) {
      russianLangFile.getParentFile().mkdirs();
      try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("russian-lang.yml")) {
        Files.copy(inputStream, russianLangFile.toPath());
      } catch (Exception e) {
        getLogger().severe("Failed to create custom configuration file: " + e.getMessage());
      }
    }

    // Load the custom configuration file
    russianLang = YamlConfiguration.loadConfiguration(russianLangFile);

    germanLangFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + "/lang/" + File.separator , "german-lang.yml");
    if (!germanLangFile.exists()) {
      germanLangFile.getParentFile().mkdirs();
      try (InputStream inputStream = Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getResource("german-lang.yml")) {
        Files.copy(inputStream, germanLangFile.toPath());
      } catch (Exception e) {
        getLogger().severe("Failed to create custom configuration file: " + e.getMessage());
      }
    }

    // Load the custom configuration file
    germanLang = YamlConfiguration.loadConfiguration(germanLangFile);
  }

  public static FileConfiguration getMainConfig() {
    return mainConfig;
  }

  public static FileConfiguration getOrbCon(){
    return orbconfig;
  }

  public static FileConfiguration getEnglishLang(){
    return englishLang;
  }

  public static FileConfiguration getGermanLang(){
    return germanLang;
  }

  public static FileConfiguration getRussianLang(){
    return russianLang;
  }

}
