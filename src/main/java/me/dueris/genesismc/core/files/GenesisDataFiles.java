package me.dueris.genesismc.core.files;

import it.unimi.dsi.fastutil.Hash;
import me.dueris.genesismc.custom_origins.CustomOrigins;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.lang.model.util.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.DirectoryStream;
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


    customOriginYml.delete();
    try {
      customOriginYml.createNewFile();
    }catch (IOException e){
      //my hands don't hurt
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to CustomOriginIdentifiers.yml. Please reload or restart server. If that doesn't work, contact Dueris on her Discord server");
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
    getPlugCon().addDefault("dragonborne-disable", false);
    getPlugCon().addDefault("custom-origins", true);
    getPlugCon().addDefault("orb-of-origins-enabled", true);
    getPlugCon().addDefault("water-prot-enabled", true);
    getPlugCon().addDefault("choose-onjoin", true);
    getPlugCon().addDefault("(", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    getPlugCon().addDefault("Support Discord", "https://discord.gg/RKmQnU6SRt");
    getPlugCon().addDefault("Donation Link", "https://streamelements.com/purplewolfmc/tip");
    getPlugCon().addDefault("Open Source", "https://github.com/Dueris/GenesisMC-Minecraft_Plugin");
    getPlugCon().addDefault("Issues", "https://github.com/Dueris/GenesisMC-Minecraft_Plugin/issues");
    getPlugCon().addDefault(")", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");


    getOrbCon().addDefault("name", ChatColor.LIGHT_PURPLE + "Orb of Origins");
    getOrbCon().addDefault("orb-of-origins-enabled", true);

    getCustomOriginConfig().options().setHeader(Collections.singletonList("DO NOT TOUCH, YOU HAVE BEEN WARNED. YOU CAN DELETE BUT NO TOUCH!"));
    readCustomOriginDatapacks();
  }

  public static void readCustomOriginDatapacks() {
    File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
    File[] originDatapacks = originDatapackDir.listFiles();

    if (originDatapacks == null) return;

    int i = 100;
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
          getCustomOriginConfig().addDefault("Origins." + i, originDatapack.getName() + ":" + originFolder + ":" + originFileName);
          i++;
        }

      } catch (Exception e) {
        //e.printStackTrace();
        Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + originDatapack.getName() + ". Is it a valid origin file?");
      }
    }
  }

  public static HashMap<Integer, String> getCustomOrigins_OriginID_Identifier() {
    HashMap<Integer, String> customOrigins = new HashMap<>();
    if (!getCustomOriginConfig().contains("Origins")) return null;
    for (String key : getCustomOriginConfig().getConfigurationSection("Origins").getKeys(true)) {
      customOrigins.put(Integer.valueOf(key), getCustomOriginConfig().getString("Origins."+key));
    }
    return customOrigins;
    //returns the identifier GenesisDataFiles.getCustomOrigins().values();
    //returns the origin id GenesisDataFiles.getCustomOrigins().keySet();
    //return the entire hash map GenesisDataFiles.getCustomOrigins();
  }

  public static ArrayList<Integer> getCustomOriginIds() {
    ArrayList<Integer> originIds = new ArrayList<Integer>();
    if (!getCustomOriginConfig().contains("Origins")) return null;
    for (String key : getCustomOriginConfig().getConfigurationSection("Origins").getKeys(true)) {
      originIds.add(Integer.valueOf(key));
    }
    return originIds;
  }

  public static ArrayList<String> getCustomOriginIdentifier() {
    ArrayList<String> originIdentifiers = new ArrayList<String>();
    if (!getCustomOriginConfig().contains("Origins")) return null;
    for (String key : getCustomOriginConfig().getConfigurationSection("Origins").getKeys(true)) {
      originIdentifiers.add(getCustomOriginConfig().getString("Origins."+key));
    }
    return originIdentifiers;
  }

  public static String getCustomOriginName(int originId) {
    String originName = null;

    String value = getCustomOriginConfig().getString("Origins."+originId);
    if (value == null) return null;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
    JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
    originName = (String) parser.get("name");
    } catch (Exception e) {
      //e.printStackTrace();
      Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse \"name\" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }
    return originName;
  }

  public static String getCustomOriginIcon(int originId) {
    String originIcon = null;

    String value = getCustomOriginConfig().getString("Origins."+originId);
    if (value == null) return null;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
      JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
      originIcon = (String) parser.get("icon");
    } catch (Exception e) {
      //e.printStackTrace();
      Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse \"icon\" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }
    return originIcon;
  }

  public static Long getCustomOriginImpact(int originId) {
    Long originImpact = null;

    String value = getCustomOriginConfig().getString("Origins."+originId);
    if (value == null) return null;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
      JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
      originImpact = (Long) parser.get("impact");
    } catch (Exception e) {
      //e.printStackTrace();
      Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse \"impact\" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }
    return originImpact;
  }

  public static String getCustomOriginDescription(int originId) {
    String originDescription = null;

    String value = getCustomOriginConfig().getString("Origins."+originId);
    if (value == null) return null;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
      JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
      originDescription = (String) parser.get("description");
    } catch (Exception e) {
      //e.printStackTrace();
      Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse \"description\" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }
    return originDescription;
  }

  public static ArrayList<String> getCustomOriginPowers(int originId) {
    ArrayList<String> originPowers = new ArrayList<String>();

    String value = getCustomOriginConfig().getString("Origins."+originId);
    if (value == null) return null;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
      JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
      JSONArray powers = ((JSONArray)parser.get("powers"));
      for (Object power : powers) originPowers.add((String) power);
    } catch (Exception e) {
      //e.printStackTrace();
      Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse \"powers\" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }

    return originPowers;
  }

  public static boolean getCustomOriginHidden(int originId) {
    boolean originHidden = false;

    String value = getCustomOriginConfig().getString("Origins."+originId);
    if (value == null) return false;

    String[] values = value.split(":");
    File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+values[0]+"/data/"+values[1]+"/origins/"+values[2]+".json");
    try {
      JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
      if (parser.containsKey("hidden")) {
        originHidden = (boolean) parser.get("hidden");
      }
    } catch (Exception e) {
      //e.printStackTrace();
      Bukkit.getServer().getConsoleSender().sendMessage("Failed to parse \"hidden\" in data/"+values[1]+"/origins/"+values[2]+".json file for " + values[1] + ". Is it a valid origin file?");
    }
    return originHidden;
  }

}
