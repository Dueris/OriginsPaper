package me.dueris.genesismc.core.files;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GenesisDataFiles {

  private static File pluginconyml;
  private static File orbconyml;

  private static FileConfiguration pluginConfig;
  private static FileConfiguration orbConfig;

  //setup config file
  public static void setup() {

    File custom_folder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");

    orbconyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "orboforigins.yml");
    pluginconyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origin-server.yml");

    if (!custom_folder.exists()) {
      custom_folder.mkdirs();
    }

    if (!pluginconyml.exists()) {
      try {
        pluginconyml.createNewFile();
      } catch (IOException e) {
        //my hands hurt
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create origin-server.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if (!orbconyml.exists()) {
      try {
        orbconyml.createNewFile();
      } catch (IOException e) {
        //my hands hurt
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create orboforigins.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    pluginConfig = YamlConfiguration.loadConfiguration(pluginconyml);
    orbConfig = YamlConfiguration.loadConfiguration(orbconyml);
  }

  public static FileConfiguration getPlugCon() {
    return pluginConfig;
  }

  public static FileConfiguration getOrbCon() {
    return orbConfig;
  }

  public static void save() {
    try {
      pluginConfig.save(pluginconyml);
    } catch (IOException e) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file. Please try again");
    }
    try {
      orbConfig.save(orbconyml);
    } catch (IOException e) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file. Please try again");
    }
  }

  public static void reload() {
    pluginConfig = YamlConfiguration.loadConfiguration(pluginconyml);
    orbConfig = YamlConfiguration.loadConfiguration(orbconyml);
  }


  public static void setDefaults() {
    //PluginConfig

    getPlugCon().addDefault("beta-enabled", false);
    getPlugCon().addDefault("origins-expanded", false);
    getPlugCon().addDefault("use-plugin-detection", true);
    getPlugCon().addDefault("use-builtin-api", true);
    getPlugCon().addDefault("config-version", "1016788");
    getPlugCon().addDefault("console-dump-onstartup", false);
    getPlugCon().addDefault("human-disable", false);
    getPlugCon().addDefault("enderian-disable", false);
    getPlugCon().addDefault("shulk-disable", false);
    getPlugCon().addDefault("arachnid-disable", false);
    getPlugCon().addDefault("creep-disable", false);
    getPlugCon().addDefault("phantom-disable", false);
    getPlugCon().addDefault("slimeling-disable", false);
    getPlugCon().addDefault("feline-disable", false);
    getPlugCon().addDefault("blazeborn-disable", false);
    getPlugCon().addDefault("starborne-disable", false);
    getPlugCon().addDefault("merling-disable", false);
    getPlugCon().addDefault("allay-disable", false);
    getPlugCon().addDefault("rabbit-disable", false);
    getPlugCon().addDefault("bumblebee-disable", false);
    getPlugCon().addDefault("elytrian-disable", false);
    getPlugCon().addDefault("avian-disable", false);
    getPlugCon().addDefault("piglin-disable", false);
    getPlugCon().addDefault("sculkling-disable", false);
    getPlugCon().addDefault("custom-origins", true);
    getPlugCon().addDefault("orb-of-origins-enabled", true);
    getPlugCon().addDefault("water-prot-enabled", true);
    getPlugCon().addDefault("choose-onjoin", true);
    getPlugCon().addDefault("(", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    getPlugCon().addDefault("Support Discord", "https://discord.gg/RKmQnU6SRt");
    getPlugCon().addDefault("Donation Link", "https://streamelements.com/duerismc/tip");
    getPlugCon().addDefault("Open Source", "https://github.com/Dueris/GenesisMC-Minecraft_Plugin");
    getPlugCon().addDefault("Issues", "https://github.com/Dueris/GenesisMC-Minecraft_Plugin/issues");
    getPlugCon().addDefault(")", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");


    getOrbCon().addDefault("name", ChatColor.LIGHT_PURPLE + "Orb of Origins");
    getOrbCon().addDefault("orb-of-origins-enabled", true);
  }
}
