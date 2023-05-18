package me.dueris.genesismc.core.api.factory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CustomOriginAPI {

    private static HashMap<String, String> customOrigins = new HashMap<>();

    public static HashMap<String, String> getCustomOrigins() {
        return customOrigins;
    }

    public static void removeUnzippedOriginDatapacks() {
        File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
        File[] originDatapacks = originDatapackDir.listFiles();
        if (originDatapacks == null) return;

        for (File file : originDatapacks) {
            try {
                if (file.getName().startsWith(".")) FileUtils.deleteDirectory(new File(file.toURI())); //Linux
                if (SystemUtils.IS_OS_WINDOWS) {
                    if (Boolean.parseBoolean(Files.getAttribute(Path.of(file.getAbsolutePath()), "dos:hidden", LinkOption.NOFOLLOW_LINKS).toString())) FileUtils.deleteDirectory(new File(file.toURI())); //Windows
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] Error trying to remove old custom origin \""+file.getName()+"\" - This file was automatically unzipped by Genesis.");
            }
        }
    }

    public static void unzipCustomOriginDatapacks() {
        File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
        File[] originDatapacks = originDatapackDir.listFiles();
        if (originDatapacks == null) return;

        for (File file : originDatapacks) {
            if (!FilenameUtils.getExtension(file.getName()).equals("zip")) continue;

            try {
                File unzippedDestinationFile = new File(file.getAbsolutePath());
                Path destination;
                if (SystemUtils.IS_OS_WINDOWS) {
                    destination = Path.of(FilenameUtils.removeExtension(unzippedDestinationFile.getPath()) + "_UnzippedByGenesis");
                    Files.setAttribute(destination, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
                }
                else destination = Path.of(FilenameUtils.removeExtension(unzippedDestinationFile.getParent()+"/."+unzippedDestinationFile.getName())+"_UnzippedByGenesis");

                if (!Files.exists(destination)) Files.createDirectory(destination);
                else continue;

                FileInputStream fileInputStream = new FileInputStream(file);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {

                    Path path = destination.resolve(zipEntry.getName());
                    if (!path.startsWith(destination)) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] Something went wrong ¯\\_(ツ)_/¯");

                    if (zipEntry.isDirectory()) Files.createDirectories(path);
                    else {
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(path));
                        byte[] bytes = zipInputStream.readAllBytes();
                        bufferedOutputStream.write(bytes, 0, bytes.length);
                        bufferedOutputStream.close();
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
                zipInputStream.close();
                zipInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadCustomOriginDatapacks() {
        File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
        File[] originDatapacks = originDatapackDir.listFiles();
        if (originDatapacks == null) return;

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

                    if (!customOrigins.containsKey(originFolder+":"+originFileName)) {
                        customOrigins.put(originFolder+":"+originFileName, originDatapack.getName());
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] Duplicate origin detected!");
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] \""+originFolder+":"+originFileName+"\" in \""+originDatapack.getName()+"\" (This one won't load).");
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] \""+originFolder+":"+originFileName+"\" in \""+customOrigins.get(originFolder+":"+originFileName)+"\" (This one will still load).");
                    }
                }

            } catch (Exception e) {
                //e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + originDatapack.getName() + ". Is it a valid origin file?");
            }
        }
    }

    public static ArrayList<String> getCustomOriginTags() {
        return new ArrayList<>(customOrigins.keySet());
    }

    public static ArrayList<String> getCustomOriginIdentifiers() {
        return new ArrayList<>(customOrigins.values());
    }

    public static Object getCustomOriginDetail(String originTag, String valueToParse) {
        String[] values = originTag.split(":");
        String dirName = customOrigins.get(originTag);
        File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+dirName+"/data/"+values[0]+"/origins/"+values[1]+".json");
        try {
            JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
            return parser.get(valueToParse);
        } catch (Exception e) {
            //e.printStackTrace();
            //Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Using origin defaults for "+originTag+" - \""+e.getMessage()+"\"");
            return null;
        }
    }

    public static String getCustomOriginName(String originTag) {
        Object value = getCustomOriginDetail(originTag, "name");
        if (value == null) return "No Name";
        return (String) value;
    }

    public static String getCustomOriginIcon(String originTag) {
        Object value = getCustomOriginDetail(originTag, "icon");
        if (value == null || value.equals("minecraft:air")) return "minecraft:player_head";
        if (value instanceof JSONObject) {
            try {
                JSONObject parser = (JSONObject) new JSONParser().parse(((JSONObject) value).toJSONString());
                value = parser.get("item");
                System.out.println(value);
            } catch (Exception e) {return "minecraft:player_head";}
        }
        return (String) value;
    }

    public static Long getCustomOriginImpact(String originTag) {
        Object value = getCustomOriginDetail(originTag, "impact");
        if (value == null) return 1L;
        return (Long) value;
    }

    public static String getCustomOriginDescription(String originTag) {
        Object value = getCustomOriginDetail(originTag, "description");
        if (value == null) return "No Description";
        return (String) value;
    }

    public static ArrayList<String> getCustomOriginPowers(String originTag) {
        Object value = getCustomOriginDetail(originTag, "powers");
        if (value == null) return new ArrayList<String>(List.of());
        return (ArrayList<String>) value;
    }

    public static boolean getCustomOriginUnChoosable(String originTag) {
        Object value = getCustomOriginDetail(originTag, "unchoosable");
        if (value == null) return false;
        return (Boolean) value;
    }

    public static Object getOriginPowerDetial(String originTag, String powerTag, String valueToParse) {
        String[] values = powerTag.split(":");

        if (Objects.equals(values[0], "origins")) {
            return null;
            //temporary way of dealing with build in origin powers
        }

        String dirName = customOrigins.get(originTag);
        File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+dirName+"/data/"+values[0]+"/powers/"+values[1]+".json");
        try {
            JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
            return parser.get(valueToParse);
        } catch (Exception e) {
            //e.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Using power defaults for "+powerTag+" - \""+e.getMessage()+"\"");

            return null;
        }
    }

    public static String getCustomOriginPowerName(String originTag, String powerTag) {
        if (powerTag.equals("origins:fall_immunity")) return "Acrobatics";
        if (powerTag.equals("origins:aerial_combatant")) return "Aerial Combatant";
        if (powerTag.equals("origins:aqua_affinity")) return "Aqua Affinity";
        if (powerTag.equals("origins:aquatic")) return "Hidden";
        if (powerTag.equals("origins:arthropod")) return "Hidden";
        if (powerTag.equals("origins:more_kinetic_damage")) return "Brittle Bones";
        if (powerTag.equals("origins:burning_wrath")) return "Burning Wrath";
        if (powerTag.equals("origins:carnivore")) return "Carnivore";
        if (powerTag.equals("origins:scare_creepers")) return "Catlike Appearance";
        if (powerTag.equals("origins:claustrophobia")) return "Claustrophobia";
        if (powerTag.equals("origins:climbing")) return "Climbing";
        if (powerTag.equals("origins:hunger_over_time")) return "Fast Metabolism";
        if (powerTag.equals("origins:slow_falling")) return "Featherweight";
        if (powerTag.equals("origins:swim_speed")) return "Fins";
        if (powerTag.equals("origins:fire_immunity")) return "Fire Immunity";
        if (powerTag.equals("origins:fragile")) return "Fragile";
        if (powerTag.equals("origins:fresh_air")) return "Fresh Air";
        if (powerTag.equals("origins:launch_into_air")) return "Gift of the Winds";
        if (powerTag.equals("origins:water_breathing")) return "Gills";
        if (powerTag.equals("origins:shulker_inventory")) return "Hoarder";
        if (powerTag.equals("origins:hotblooded")) return "Hotblooded";
        if (powerTag.equals("origins:water_vulnerability")) return "Hydrophobia";
        if (powerTag.equals("origins:invisibility")) return "Invisibility";
        if (powerTag.equals("origins:more_exhaustion")) return "Large Appetite";
        if (powerTag.equals("origins:like_air")) return "Like Air";
        if (powerTag.equals("origins:like_water")) return "Like Water";
        if (powerTag.equals("origins:master_of_webs")) return "Master of Webs";
        if (powerTag.equals("origins:light_armor")) return "Need for Mobility";
        if (powerTag.equals("origins:nether_spawn")) return "Nether Inhabitant";
        if (powerTag.equals("origins:nine_lives")) return "Nine Lives";
        if (powerTag.equals("origins:cat_vision")) return "Nocturnal";
        if (powerTag.equals("origins:lay_eggs")) return "Oviparous";
        if (powerTag.equals("origins:phasing")) return "Phasing";
        if (powerTag.equals("origins:burn_in_daylight")) return "Photoallergic";
        if (powerTag.equals("origins:arcane_skin")) return "Arcane Skin";
        if (powerTag.equals("origins:end_spawn")) return "End Inhabitant";
        if (powerTag.equals("origins:phantomize_overlay")) return "Hidden";
        if (powerTag.equals("origins:pumpkin_hate")) return "Scared of Gourds";
        if (powerTag.equals("origins:extra_reach")) return "Slender Body";
        if (powerTag.equals("origins:sprint_jump")) return "Strong Ankles";
        if (powerTag.equals("origins:strong_arms")) return "Strong Arms";
        if (powerTag.equals("origins:natural_armor")) return "Sturdy Skin";
        if (powerTag.equals("origins:tailwind")) return "Tailwind";
        if (powerTag.equals("origins:throw_ender_pearl")) return "Teleportation";
        if (powerTag.equals("origins:translucent")) return "Translucent";
        if (powerTag.equals("origins:no_shield")) return "Unwieldy";
        if (powerTag.equals("origins:vegetarian")) return "Vegetarian";
        if (powerTag.equals("origins:velvet_paws")) return "Velvet Paws";
        if (powerTag.equals("origins:weak_arms")) return "Weak Arms";
        if (powerTag.equals("origins:webbing")) return "Webbing";
        if (powerTag.equals("origins:water_vision")) return "Wet Eyes";
        if (powerTag.equals("origins:elytra")) return "Winged";
        if (powerTag.equals("origins:air_from_potions")) return "Hidden";
        if (powerTag.equals("origins:conduit_power_on_land")) return "Hidden";
        if (powerTag.equals("origins:damage_from_potions")) return "Hidden";
        if (powerTag.equals("origins:damage_from_snowballs")) return "Hidden";
        if (powerTag.equals("origins:ender_particles")) return "Hidden";
        if (powerTag.equals("origins:flame_particles")) return "Hidden";
        if (powerTag.equals("origins:no_cobweb_slowdown")) return "Hidden";
        if (powerTag.equals("origins:phantomize")) return "Phantom Form";
        if (powerTag.equals("origins:strong_arms_break_speed")) return "Hidden";

        if (powerTag.equals("genesis:hot_hands")) return "Hot Hands";
        if (powerTag.equals("genesis:extra_fire_tick")) return "Flammable";
        if (powerTag.equals("genesis:silk_touch")) return "Delicate Touch";
        if (powerTag.equals("genesis:bow_inability")) return "Horrible Coordination";
        Object value = getOriginPowerDetial(originTag, powerTag, "name");
        if (value == null) return "No Name";
        return (String) value;
    }

    public static String getCustomOriginPowerDescription(String originTag, String powerTag) {
        if (powerTag.equals("origins:fall_immunity")) return "You never take fall damage, no matter from which height you fall.";
        if (powerTag.equals("origins:aerial_combatant")) return "You deal substantially more damage while in Elytra flight.";
        if (powerTag.equals("origins:aqua_affinity")) return "You may break blocks underwater as others do on land.";
        if (powerTag.equals("origins:aquatic")) return "Hidden";
        if (powerTag.equals("origins:arthropod")) return "Hidden";
        if (powerTag.equals("origins:more_kinetic_damage")) return "You take more damage from falling and flying into blocks.";
        if (powerTag.equals("origins:burning_wrath")) return "When on fire, you deal additional damage with your attacks.";
        if (powerTag.equals("origins:carnivore")) return "Your diet is restricted to meat, you can't eat vegetables.";
        if (powerTag.equals("origins:scare_creepers")) return "Creepers are scared of you and will only explode if you attack them first.";
        if (powerTag.equals("origins:claustrophobia")) return "Being somewhere with a low ceiling for too long will weaken you ad make you slower.";
        if (powerTag.equals("origins:climbing")) return "You are able to climb up any kind of wall, just not ladders.";
        if (powerTag.equals("origins:hunger_over_time")) return "Being phantomized causes you to become hungry";
        if (powerTag.equals("origins:slow_falling")) return "You fall as gently to the ground as a feather would, unless you sneak.";
        if (powerTag.equals("origins:swim_speed")) return "Your underwater speed is increased.";
        if (powerTag.equals("origins:fire_immunity")) return "You are immune to all types of fire damage.";
        if (powerTag.equals("origins:fragile")) return "You have 3 less hearts of health than humans.";
        if (powerTag.equals("origins:fresh_air")) return "When sleeping, your bed needs to be at an altitude of at least 86 blocks, so you can breathe fresh air.";
        if (powerTag.equals("origins:launch_into_air")) return "Every 30 seconds, you are able to launch about 20 blocks up into the air";
        if (powerTag.equals("origins:water_breathing")) return "You can breathe underwater, but not on land";
        if (powerTag.equals("origins:shulker_inventory")) return "You have access ti an additional 9 slots of inventory, which keep the items on death.";
        if (powerTag.equals("origins:hotblooded")) return "Due to your hot body, venom's burn up, making you immune to poison and hunger status effects.";
        if (powerTag.equals("origins:water_vulnerability")) return "You receive damage over time while in contact with water.";
        if (powerTag.equals("origins:invisibility")) return "While phantomized, you are invisible.";
        if (powerTag.equals("origins:more_exhaustion")) return "You exhaust much quicker than others., thus requiring you to eat more.";
        if (powerTag.equals("origins:like_air")) return "Modifiers to your walking speed also apply while you're airborne.";
        if (powerTag.equals("origins:like_water")) return "When underwater, you do not sink to the ground unless you want to.";
        if (powerTag.equals("origins:master_of_webs")) return "You navigate cobwebs perfectly, and are able to climb in them. When you hit an enemy in melee, they get stuck in cobweb for a while. Non-arthropods stuck in cobweb will be sensed by you. You are able to craft cobwebs from string.";
        if (powerTag.equals("origins:light_armor")) return "You can not wear any heavy armour (armour with protection values higher than chainmail).";
        if (powerTag.equals("origins:nether_spawn")) return "Your natural spawn will be in the Nether.";
        if (powerTag.equals("origins:nine_lives")) return "You have 1 less heart of health than humans.";
        if (powerTag.equals("origins:cat_vision")) return "you can slightly see in the dark when not in water.";
        if (powerTag.equals("origins:lay_eggs")) return "Whenever you wake up in the morning, you will lay an egg.";
        if (powerTag.equals("origins:phasing")) return "While phantomized, you can walk though solid material, expect Obsidian";
        if (powerTag.equals("origins:burn_in_daylight")) return "You begin to burn in daylight if you are not invisible.";
        if (powerTag.equals("origins:arcane_skin")) return "Your skin is a dark blue hue";
        if (powerTag.equals("origins:end_spawn")) return "Your journey begins in the End";
        if (powerTag.equals("origins:phantomize_overlay")) return "Hidden";
        if (powerTag.equals("origins:pumpkin_hate")) return "You are afraid of pumpkins. For a good reason.";
        if (powerTag.equals("origins:extra_reach")) return "You can reach blocks and entities further away.";
        if (powerTag.equals("origins:sprint_jump")) return "You are able to jump higher by jumping while sprinting.";
        if (powerTag.equals("origins:strong_arms")) return "You are strong enough to break natural stones without using a pickaxe.";
        if (powerTag.equals("origins:natural_armor")) return "Even without wearing armor, your skin provides natural protection.";
        if (powerTag.equals("origins:tailwind")) return "You are a little bit quicker on foot than others.";
        if (powerTag.equals("origins:throw_ender_pearl")) return "Whenever you want, you may throw an ender pearl, which deals no damage, allowing you to teleport.";
        if (powerTag.equals("origins:translucent")) return "Your skin is translucent.";
        if (powerTag.equals("origins:no_shield")) return "The way your hands are formed provides no way of holding a shield upright.";
        if (powerTag.equals("origins:vegetarian")) return "You can't digest any meat";
        if (powerTag.equals("origins:velvet_paws")) return "Your footsteps don't cause any vibrations which could otherwise be picked up by nearby lifeforms.";
        if (powerTag.equals("origins:weak_arms")) return "When no under the effect of a strength potion, you can only mine natural stone if there are at most 2 other natural stone blocks adjacent to it.";
        if (powerTag.equals("origins:webbing")) return "When you hit an enemy in melee, they get stuck in cobwebs.";
        if (powerTag.equals("origins:water_vision")) return "Your vision underwater is perfect.";
        if (powerTag.equals("origins:elytra")) return "You have Elytra wings without needing to equip any";
        if (powerTag.equals("origins:air_from_potions")) return "Hidden";
        if (powerTag.equals("origins:conduit_power_on_land")) return "Hidden";
        if (powerTag.equals("origins:damage_from_potions")) return "Hidden";
        if (powerTag.equals("origins:damage_from_snowballs")) return "Hidden";
        if (powerTag.equals("origins:ender_particles")) return "Hidden";
        if (powerTag.equals("origins:flame_particles")) return "Hidden";
        if (powerTag.equals("origins:no_cobweb_slowdown")) return "Hidden";
        if (powerTag.equals("origins:phantomize")) return "You can switch between human and phantom form at wil, but only while you are saturated enough to sprint.";
        if (powerTag.equals("origins:strong_arms_break_speed")) return "Hidden";

        if (powerTag.equals("genesis:hot_hands")) return "Your punches set mobs alight.";
        if (powerTag.equals("genesis:extra_fire_tick")) return "You take 50% more damage from fire";
        if (powerTag.equals("genesis:silk_touch")) return "You have silk touch hands";
        if (powerTag.equals("genesis:bow_inability")) return "You are not able to use a bow, you are WAY too clumsy";
        Object value = getOriginPowerDetial(originTag, powerTag, "description");
        if (value == null) return "No Description";
        return (String) value;
    }

    public static boolean getCustomOriginPowerHidden(String originTag, String powerTag) {
        if (powerTag.equals("origins:aquatic")) return true;
        if (powerTag.equals("origins:arthropod")) return true;
        if (powerTag.equals("origins:phantomize_overlay")) return true;
        if (powerTag.equals("origins:air_from_potions")) return true;
        if (powerTag.equals("origins:conduit_power_on_land")) return true;
        if (powerTag.equals("origins:damage_from_potions")) return true;
        if (powerTag.equals("origins:damage_from_snowballs")) return true;
        if (powerTag.equals("origins:ender_particles")) return true;
        if (powerTag.equals("origins:flame_particles")) return true;
        if (powerTag.equals("origins:no_cobweb_slowdown")) return true;
        if (powerTag.equals("origins:strong_arms_break_speed")) return true;
        Object value = getOriginPowerDetial(originTag, powerTag, "hidden");
        if (value == null) return false;
        return (Boolean) value;
    }
}
