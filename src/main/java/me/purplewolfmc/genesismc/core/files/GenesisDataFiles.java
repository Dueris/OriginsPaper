package me.purplewolfmc.genesismc.core.files;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GenesisDataFiles {
  private static File enderyml;
  private static File humanyml;
  private static File shulkyml;
  private static File arachnidyml;
  private static File creepyml;
  private static File phantomyml;
  private static File slimelingyml;
  private static File vexianyml;
  private static File blazebornyml;
  private static File starborneyml;
  private static File merlingyml;
  private static File allayyml;
  private static File rabbityml;
  private static File bumblebeeyml;
  private static File elytrianyml;
  private static File avianyml;
  private static File piglinyml;
  private static File dragonborneyml;
  private static File orbyml;
  private static File betayml;
  private static File menuyml;
  private static File pluginconyml;

  private static FileConfiguration enderianConfig;
  private static FileConfiguration humanConfig;
  private static FileConfiguration shulkConfig;
  private static FileConfiguration arachnidConfig;
  private static FileConfiguration creepConfig;
  private static FileConfiguration phantomConfig;
  private static FileConfiguration slimelingConfig;
  private static FileConfiguration vexianConfig;
  private static FileConfiguration blazebornConfig;
  private static FileConfiguration starborneConfig;
  private static FileConfiguration merlingConfig;
  private static FileConfiguration allayConfig;
  private static FileConfiguration rabbitConfig;
  private static FileConfiguration bumblebeeConfig;
  private static FileConfiguration elytrianConfig;
  private static FileConfiguration avianConfig;
  private static FileConfiguration piglinConfig;
  private static FileConfiguration dragonborneConfig;
  private static FileConfiguration orbConfig;
  private static FileConfiguration betaConfig;
  private static FileConfiguration menuConfig;
  private static FileConfiguration pluginConfig;


  //setup config file
  public static void setup(){

    File originsFolder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "origins");
    File betaFolder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "beta");
    File choosingmenuFolder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "choosing_menu");
    File itemFolder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "items");
    File custom_folder = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
    enderyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/enderian.yml");
    slimelingyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/slimeling.yml");
    phantomyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/phantom.yml");
    creepyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/creep.yml");
    shulkyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/shulk.yml");
    humanyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/human.yml");
    orbyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/items/orb_of_origins.yml");
    betayml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/beta/beta.yml");
    menuyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/choosing_menu/menu.yml");
    pluginconyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "plugin-config.yml");
    vexianyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/vexian.yml");
    blazebornyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/blazeborn.yml");
    starborneyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/starborne.yml");
    merlingyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/merling.yml");
    allayyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/allay.yml");
    rabbityml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/rabbit.yml");
    bumblebeeyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/bumblebee.yml");
    elytrianyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/elytrian.yml");
    avianyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/avian.yml");
    piglinyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/piglin.yml");
    dragonborneyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/dragonborne.yml");
    arachnidyml = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "/origins/arachnid.yml");



    if(!originsFolder.exists()){
      originsFolder.mkdirs();
    }
    if(!betaFolder.exists()){
      betaFolder.mkdirs();
    }
    if(!choosingmenuFolder.exists()){
      choosingmenuFolder.mkdirs();
    }
    if(!itemFolder.exists()){
      itemFolder.mkdirs();
    }
    if(!custom_folder.exists()){
      custom_folder.mkdirs();
    }


    if(!enderyml.exists()){
      try{
        enderyml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!humanyml.exists()){
      try{
        humanyml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!shulkyml.exists()){
      try{
        shulkyml.createNewFile();
      }catch (IOException e){
        //why
      }
    }
    if(!creepyml.exists()){
      try{
        creepyml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!phantomyml.exists()){
      try{
        phantomyml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!slimelingyml.exists()){
      try{
        slimelingyml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!orbyml.exists()){
      try{
        orbyml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!betayml.exists()){
      try{
        betayml.createNewFile();
      }catch (IOException e){
        //what am i even doin.
      }
    }
    if(!menuyml.exists()){
      try{
        menuyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!pluginconyml.exists()){
      try{
        pluginconyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!vexianyml.exists()){
      try{
        vexianyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!blazebornyml.exists()){
      try{
        blazebornyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!starborneyml.exists()){
      try{
        starborneyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!merlingyml.exists()){
      try{
        merlingyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!allayyml.exists()){
      try{
        allayyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!rabbityml.exists()){
      try{
        rabbityml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!bumblebeeyml.exists()){
      try{
        bumblebeeyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!elytrianyml.exists()){
      try{
        elytrianyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!avianyml.exists()){
      try{
        avianyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!piglinyml.exists()){
      try{
        piglinyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!dragonborneyml.exists()){
      try{
        dragonborneyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }
    if(!arachnidyml.exists()){
      try{
        arachnidyml.createNewFile();
      }catch (IOException e){
        //my hands hurt
      }
    }

    enderianConfig = YamlConfiguration.loadConfiguration(enderyml);
    humanConfig = YamlConfiguration.loadConfiguration(humanyml);
    shulkConfig = YamlConfiguration.loadConfiguration(shulkyml);
    creepConfig = YamlConfiguration.loadConfiguration(creepyml);
    phantomConfig = YamlConfiguration.loadConfiguration(phantomyml);
    slimelingConfig = YamlConfiguration.loadConfiguration(slimelingyml);
    orbConfig = YamlConfiguration.loadConfiguration(orbyml);
    betaConfig = YamlConfiguration.loadConfiguration(betayml);
    menuConfig = YamlConfiguration.loadConfiguration(menuyml);
    pluginConfig = YamlConfiguration.loadConfiguration(pluginconyml);
    vexianConfig = YamlConfiguration.loadConfiguration(vexianyml);
    blazebornConfig = YamlConfiguration.loadConfiguration(blazebornyml);
    starborneConfig = YamlConfiguration.loadConfiguration(starborneyml);
    merlingConfig = YamlConfiguration.loadConfiguration(merlingyml);
    allayConfig = YamlConfiguration.loadConfiguration(allayyml);
    rabbitConfig = YamlConfiguration.loadConfiguration(rabbityml);
    bumblebeeConfig = YamlConfiguration.loadConfiguration(bumblebeeyml);
    elytrianConfig = YamlConfiguration.loadConfiguration(elytrianyml);
    avianConfig = YamlConfiguration.loadConfiguration(avianyml);
    piglinConfig = YamlConfiguration.loadConfiguration(piglinyml);
    dragonborneConfig = YamlConfiguration.loadConfiguration(dragonborneyml);
    arachnidConfig = YamlConfiguration.loadConfiguration(arachnidyml);

  }
  public static FileConfiguration get1(){
    return humanConfig;
  }
  public static FileConfiguration get2(){
    return shulkConfig;
  }
  public static FileConfiguration get3(){
    return creepConfig;
  }
  public static FileConfiguration get4(){
    return phantomConfig;
  }
  public static FileConfiguration get5(){
    return slimelingConfig;
  }
  public static FileConfiguration get(){
    return enderianConfig;
  }
  public static FileConfiguration getOrb(){
    return orbConfig;
  }
  public static FileConfiguration getBeta(){
    return betaConfig;
  }
  public static FileConfiguration getMenu(){
    return menuConfig;
  }
  public static FileConfiguration getPlugCon(){
    return pluginConfig;
  }
  public static FileConfiguration get6(){
    return vexianConfig;
  }
  public static FileConfiguration get7(){
    return blazebornConfig;
  }
  public static FileConfiguration get8(){
    return starborneConfig;
  }
  public static FileConfiguration get9(){
    return merlingConfig;
  }
  public static FileConfiguration get10(){
    return allayConfig;
  }
  public static FileConfiguration get11(){
    return rabbitConfig;
  }
  public static FileConfiguration get12(){
    return bumblebeeConfig;
  }
  public static FileConfiguration get13(){
    return elytrianConfig;
  }
  public static FileConfiguration get14(){
    return avianConfig;
  }
  public static FileConfiguration get15(){
    return piglinConfig;
  }
  public static FileConfiguration get16(){
    return dragonborneConfig;
  }
  public static FileConfiguration get17(){
    return arachnidConfig;
  }

  public static void save(){
    try{
      enderianConfig.save(enderyml);
      humanConfig.save(humanyml);
      shulkConfig.save(shulkyml);
      creepConfig.save(creepyml);
      phantomConfig.save(phantomyml);
      orbConfig.save(orbyml);
      betaConfig.save(betayml);
      menuConfig.save(menuyml);
      pluginConfig.save(pluginconyml);
      slimelingConfig.save(slimelingyml);
      vexianConfig.save(vexianyml);
      blazebornConfig.save(blazebornyml);
      starborneConfig.save(starborneyml);
      merlingConfig.save(merlingyml);
      allayConfig.save(allayyml);
      rabbitConfig.save(rabbityml);
      bumblebeeConfig.save(bumblebeeyml);
      elytrianConfig.save(elytrianyml);
      avianConfig.save(avianyml);
      piglinConfig.save(piglinyml);
      dragonborneConfig.save(dragonborneyml);
      arachnidConfig.save(arachnidyml);

    }catch (IOException e){
      System.out.println("Couldn't save yml file.");
    }
  }

  public static void reload(){
    enderianConfig = YamlConfiguration.loadConfiguration(enderyml);
    humanConfig = YamlConfiguration.loadConfiguration(humanyml);
    shulkConfig = YamlConfiguration.loadConfiguration(shulkyml);
    creepConfig = YamlConfiguration.loadConfiguration(creepyml);
    phantomConfig = YamlConfiguration.loadConfiguration(phantomyml);
    slimelingConfig = YamlConfiguration.loadConfiguration(slimelingyml);
    orbConfig = YamlConfiguration.loadConfiguration(orbyml);
    betaConfig = YamlConfiguration.loadConfiguration(betayml);
    menuConfig = YamlConfiguration.loadConfiguration(menuyml);
    pluginConfig = YamlConfiguration.loadConfiguration(pluginconyml);
    vexianConfig = YamlConfiguration.loadConfiguration(vexianyml);
    blazebornConfig = YamlConfiguration.loadConfiguration(blazebornyml);
    starborneConfig = YamlConfiguration.loadConfiguration(starborneyml);
    merlingConfig = YamlConfiguration.loadConfiguration(merlingyml);
    allayConfig = YamlConfiguration.loadConfiguration(allayyml);
    rabbitConfig = YamlConfiguration.loadConfiguration(rabbityml);
    bumblebeeConfig = YamlConfiguration.loadConfiguration(bumblebeeyml);
    elytrianConfig = YamlConfiguration.loadConfiguration(elytrianyml);
    avianConfig = YamlConfiguration.loadConfiguration(avianyml);
    piglinConfig = YamlConfiguration.loadConfiguration(piglinyml);
    dragonborneConfig = YamlConfiguration.loadConfiguration(dragonborneyml);
    arachnidConfig = YamlConfiguration.loadConfiguration(arachnidyml);
  }

  public static void setDefaults(){
    //PluginConfig
    getPlugCon().addDefault("console-dump-onstartup", false);
    getPlugCon().addDefault("plugin-detection-for-incompatibilities", true);
    getPlugCon().addDefault("use-purplewolfapi", true);
    getPlugCon().addDefault("disable-origins", false);
    getPlugCon().addDefault("disable-orb_of_origins", false);
    getPlugCon().addDefault("texture-pack_enable", true);
    getPlugCon().addDefault("custom-origins", true);
    getPlugCon().addDefault("origins-expanded", false);

    //BetaConfig
    getBeta().addDefault("update-beta", false);
    //MenuConfig

    //ItemConfig

    //orboforigins
    getOrb().addDefault("disable-orb_of_origins", false);
    getOrb().addDefault("name", ChatColor.LIGHT_PURPLE + "Orb of Origins");

    //Enderian
    get().addDefault("enderian-spawn-end", false);
    get().addDefault("enderian-disable", false);
    //Human
    get1().addDefault("human-disable", false);
    //Shulk
    get2().addDefault("shulk-disable", false);
    //Arachnid
    get17().addDefault("arachnid-disable", false);
    //Creep
    get3().addDefault("creep-disable", false);
    //Phantom
    get4().addDefault("phantom-disable", false);
    //Slimeling
    get5().addDefault("slimeling-disable", false);
    //Vexian
    get6().addDefault("vexian-disable", false);
    //Blazeborn
    get7().addDefault("blazeborn-disable", false);
    //Starborne
    get8().addDefault("starborne-disable", false);
    //Merling
    get9().addDefault("merling-disable", false);
    //Allay
    get10().addDefault("allay-disable", false);
    //Rabbit
    get11().addDefault("rabbit-disable", false);
    //Bumblebee
    get12().addDefault("bumblebee-disable", false);
    //Elytrian
    get13().addDefault("elytrian-disable", false);
    //Avian
    get14().addDefault("avian-disable", false);
    //Piglin
    get15().addDefault("piglin-disable", false);
    //Dragonborne
    get16().addDefault("dragonborne-disable", false);
  }

}
