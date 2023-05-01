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
  private static File customOriginYml;

  private static FileConfiguration pluginConfig;
  private static FileConfiguration orbConfig;
  private static FileConfiguration customOriginConfig;

  //setup config file
  public static void setup(){

    File custom_folder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");

    orbconyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "orboforigins.yml");
    pluginconyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origin-server.yml");
    customOriginYml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins/CustomOriginIdentifiers.yml");

    if(!custom_folder.exists()){
      custom_folder.mkdirs();
    }


    if (!customOriginYml.exists()) {
      try {
        customOriginYml.createNewFile();
      } catch (IOException e) {
        //my hands don't hurt
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create CustomOriginIdentifiers.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if(!pluginconyml.exists()){
      try{
        pluginconyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create origin-server.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    if(!orbconyml.exists()){
      try{
        orbconyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to create orboforigins.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
      }
    }

    pluginConfig = YamlConfiguration.loadConfiguration(pluginconyml);
    orbConfig = YamlConfiguration.loadConfiguration(orbconyml);
    customOriginConfig = YamlConfiguration.loadConfiguration(customOriginYml);

  }
  public static FileConfiguration getPlugCon(){
    return pluginConfig;
  }
  public static FileConfiguration getOrbCon(){
    return orbConfig;
  }
  public static FileConfiguration getCustomOriginConfig(){
    return customOriginConfig;
  }
  public static void save(){
    try{
      pluginConfig.save(pluginconyml);
    }catch (IOException e){
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file. Please try again");
    }
    try{
      orbConfig.save(orbconyml);
    }catch (IOException e){
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file. Please try again");
    }
    try {
      customOriginConfig.save(customOriginYml);
    } catch (IOException e) {
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save yml file. Please try again");
    }
  }

  public static void reload(){
    pluginConfig = YamlConfiguration.loadConfiguration(pluginconyml);
    orbConfig = YamlConfiguration.loadConfiguration(orbconyml);
    customOriginConfig = YamlConfiguration.loadConfiguration(customOriginYml);
  }


  public static void setDefaults(){
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
    getPlugCon().addDefault("vexian-disable", false);
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

    getCustomOriginConfig().options().setHeader(Collections.singletonList("DO NOT TOUCH, THINGS WILL BREAK!!!!!! YOU HAVE BEEN WARNED!!"));
    readCustomOriginDatapacks();
  }

  public static void readCustomOriginDatapacks() {
    File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
    File[] originDatapacks = originDatapackDir.listFiles();

    if (originDatapacks == null) return;

    int i = 100;

    if (!(getCustomOriginConfig().getConfigurationSection("origins") == null)) {
      Set<String> keys = getCustomOriginConfig().getConfigurationSection("origins").getKeys(true);
      for (String str : keys) {
        Integer key = Integer.valueOf(str);
        if (key > i) i = key + 1;
      }
    }


    for (File originDatapack : originDatapacks) {
      if (originDatapack.isFile()) continue;
      File origin_layers = new File(originDatapack.getAbsolutePath() + "/data/origins/origin_layers/origin.json");
      if (!origin_layers.exists()) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Couldn't locate \"/data/origins/origin_layers/origin.json\" for the \"" + originDatapack.getName() + "\" Origin file!");

      try {
        JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDatapack+"/data/origins/origin_layers/origin.json"));
        JSONArray origins = ((JSONArray)parser.get("origins"));

        for (Object o : origins) {
          String value = (String) o;
          String[] valueSplit = value.split(":");
          String originFolder = valueSplit[0];
          String originFileName = valueSplit[1];

          if (getCustomOriginConfig().getConfigurationSection("origins") == null || !getCustomOriginConfig().getConfigurationSection("origins").getValues(false).containsValue(originDatapack.getName() + ":" + originFolder + ":" + originFileName)) {
            getCustomOriginConfig().addDefault("origins." + i, originDatapack.getName() + ":" + originFolder + ":" + originFileName);
            i++;
          }
        }

      } catch (Exception e) {
        //e.printStackTrace();
        Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + originDatapack.getName() + ". Is it a valid origin file?");
      }
    }
  }

  public static ArrayList<Integer> getCustomOriginIds() {
    ArrayList<Integer> originIds = new ArrayList<Integer>();
    if (!getCustomOriginConfig().contains("origins")) return originIds;
    for (String key : getCustomOriginConfig().getConfigurationSection("origins").getKeys(true)) {
      originIds.add(Integer.valueOf(key));
    }
    return originIds;
  }

  public static ArrayList<String> getCustomOriginIdentifier() {
    ArrayList<String> originIdentifiers = new ArrayList<String>();
    if (!getCustomOriginConfig().contains("origins")) return originIdentifiers;
    for (String key : getCustomOriginConfig().getConfigurationSection("origins").getKeys(true)) {
      originIdentifiers.add(getCustomOriginConfig().getString("origins."+key));
    }
    return originIdentifiers;
  }

  public static Object getCustomOriginDetails(int originId, String valueToParse) {
    Object originDetail = null;

    String value = getCustomOriginConfig().getString("origins."+originId);
    if (value == null) return null;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
      JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
      originDetail = parser.get(valueToParse);
    } catch (Exception e) {
      //e.printStackTrace();
      //Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse "+valueToParse+" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }
    return originDetail;
  }

  public static String getCustomOriginName(int originId) {
    Object value = getCustomOriginDetails(originId, "name");
    if (value == null) return "NoName";
    return (String) value;
  }

  public static String getCustomOriginIcon(int originId) {
    Object value = getCustomOriginDetails(originId, "icon");
    if (value == null) return "minecraft:player_head";
    return (String) value;
  }

  public static Long getCustomOriginImpact(int originId) {
    Object value = getCustomOriginDetails(originId, "impact");
    if (value == null) return 1L;
    return (Long) value;
  }

  public static String getCustomOriginDescription(int originId) {
    Object value = getCustomOriginDetails(originId, "description");
    if (value == null) return "No Description";
    return (String) value;
  }

  public static ArrayList<String> getCustomOriginPowers(int originId) {
    Object value = getCustomOriginDetails(originId, "powers");
    if (value == null) return new ArrayList<String>(Arrays.asList("origins:nopowers"));
    return (ArrayList<String>) value;
  }

  public static boolean getCustomOriginUnChoosable(int originId) {
    Object value = getCustomOriginDetails(originId, "unchoosable");
    if (value == null) return true;
    return (Boolean) value;
  }

}
