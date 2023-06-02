package me.dueris.genesismc.core.factory;

import me.dueris.genesismc.core.utils.CustomOrigin;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CraftApoliRewriten {

    private static ArrayList<CustomOrigin> customOrigins = new ArrayList<>();

    /**
     * @return A CustomOrigin object array for all the origins that are loaded.
     **/
    public static ArrayList<CustomOrigin> getOrigins() {
        return customOrigins;
    }

    /**
     * Loads the custom origins from the datapack dir into memory.
     **/
    public static void loadOrigins() {
        File DatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + ".." + File.separator + ".." + File.separator + Bukkit.getServer().getWorlds().get(0).getName() + File.separator + "datapacks");
        File[] datapacks = DatapackDir.listFiles();
        if (datapacks == null) return;

        for (File datapack : datapacks) {
            if (datapack.isFile()) continue;
            File origin_layers = new File(datapack.getAbsolutePath() + "/data/origins/origin_layers/origin.json");
            if (!origin_layers.exists()) continue;

            String identifier = "";
            File originLayerFile = origin_layers;
            File originsFile;
            PowerContainer powers = new PowerContainer();

            String originFolder = "";
            String originFileName = "";

            try {
                JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + "/data/origins/origin_layers/origin.json"));
                JSONArray originLayer_origins = ((JSONArray) originLayerParser.get("origins"));

                for (Object o : originLayer_origins) {
                    String value = (String) o;
                    String[] valueSplit = value.split(":");
                    originFolder = valueSplit[0];
                    originFileName = valueSplit[1];

                    identifier = originFolder+":"+originFileName;
                }

                originsFile = (new File(datapack.getAbsolutePath() + "/data/"+originFolder+"/origins/"+originFileName+".json"));

                JSONObject originParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + "/data/"+originFolder+"/origins/"+originFileName+".json"));
                ArrayList<String> powersList = (ArrayList<String>) originParser.get("powers");

                for (String powerFileName : powersList) {
                    File powerFile = new File(datapack.getAbsolutePath() + "/data/"+originFolder+"/powers/"+powerFileName+".json");
                    powers.add(originFolder+":"+originFileName, powerFile, originFolder+":"+originFileName);
                }

                customOrigins.add(new CustomOrigin(identifier, originLayerFile, originsFile, powers));

            } catch (Exception e) {
                e.printStackTrace();
                //Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + datapack.getName() + ". Is it a valid origin file?");
            }
        }
    }

}
