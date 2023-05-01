package me.dueris.genesismc.custom_origins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomOriginsMethods {

    public static HashMap<String, String> customOrigins = new HashMap<>();


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
        if (value == null) return "NoName";
        return (String) value;
    }

    public static String getCustomOriginIcon(String originTag) {
        Object value = getCustomOriginDetail(originTag, "icon");
        if (value == null) return "minecraft:player_head";
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

}
