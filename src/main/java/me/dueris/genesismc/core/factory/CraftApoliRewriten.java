package me.dueris.genesismc.core.factory;

import me.dueris.genesismc.core.utils.CustomOrigin;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.nio.file.*;
//import java.nio.file;

public class CraftApoliRewriten {

    //make it load the files into memory rather than use File object (just stores filepaths)

    private static ArrayList<CustomOrigin> customOrigins = new ArrayList<>();

    /**
     * @return A CustomOrigin object array for all the origins that are loaded.
     **/
    public static ArrayList<CustomOrigin> getOrigins() {
        return customOrigins;
    }

    private static HashMap<String, Object> fileToHashMap(JSONObject JSONFileParser) {
        HashMap<String, Object> data = new HashMap<>();
        for (Object key : JSONFileParser.keySet()) data.put((String) key, JSONFileParser.get(key));
        return data;
    }


    /**
     * Loads the custom origins from the datapack dir into memory.
     **/
    public static void loadOrigins() {
        File DatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + ".." + File.separator + ".." + File.separator + Bukkit.getServer().getWorlds().get(0).getName() + File.separator + "datapacks");
        File[] datapacks = DatapackDir.listFiles();
        if (datapacks == null) return;

        for (File datapack : datapacks) {

            //zip
            if (FilenameUtils.getExtension(datapack.getName()).equals("zip")) {
                HashMap<Path, String> files = new HashMap<>();

                try {
                    ZipFile zip = new ZipFile(datapack);
                    for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                        ZipEntry entry = (ZipEntry) e.nextElement();
                        if (entry.isDirectory()) continue;
                        if (!FilenameUtils.getExtension(entry.getName()).equals("json")) continue;

                        StringBuilder out = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
                        String line;

                        while ((line = reader.readLine()) != null) out.append(line);
                        files.put(Path.of(entry.toString()), out.toString());
                    }

                    boolean originDatapack = false;
                    PowerContainer powers = new PowerContainer();
                    JSONObject originLayerParser = null;
                    String originFolder = "";
                    String originFileName = "";

                    for (Path path : files.keySet()) {
                        if (path.equals(Path.of("data"+File.separator+"origins"+File.separator+"origin_layers"+File.separator+"origin.json"))) {
                            originDatapack = true;

                            originLayerParser = (JSONObject) new JSONParser().parse(files.get(path));
                            JSONArray originLayer_origins = ((JSONArray) originLayerParser.get("origins"));

                            for (Object o : originLayer_origins) {
                                String value = (String) o;
                                String[] valueSplit = value.split(":");
                                originFolder = valueSplit[0];
                                originFileName = valueSplit[1];
                            }

                        }
                    }

                    if (!originDatapack) continue;

                    for (Path path : files.keySet())
                        if (path.equals(Path.of("data"+File.separator+originFolder+File.separator+"origins"+File.separator+originFileName+".json"))) {
                            JSONObject originParser = (JSONObject) new JSONParser().parse(files.get(path));
                            ArrayList<String> powersList = (ArrayList<String>) originParser.get("powers");

                            for (String string : powersList) {
                                String[] powerLocation = string.split(":");
                                String powerFolder = powerLocation[0];
                                String powerFileName = powerLocation[1];

                                JSONObject powerParser = (JSONObject) new JSONParser().parse(files.get(Path.of("data"+File.separator+powerFolder+File.separator+"powers"+File.separator+powerFileName+".json")));
                                powers.add(originFolder+":"+originFileName, fileToHashMap(powerParser), originFolder+":"+originFileName);
                            }

                            customOrigins.add(new CustomOrigin(originFolder+":"+originFileName, fileToHashMap(originLayerParser), fileToHashMap(originParser), powers));
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (datapack.isFile()) continue;
            }

            //non zip
            File origin_layers = new File(datapack.getAbsolutePath() + File.separator+"data"+File.separator+"origins"+File.separator+"origin_layers"+File.separator+"origin.json");
            if (!origin_layers.exists()) continue;

            PowerContainer powers = new PowerContainer();

            String originFolder = "";
            String originFileName = "";

            try {
                JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator+"data"+File.separator+"origins"+File.separator+"origin_layers"+File.separator+"origin.json"));
                JSONArray originLayer_origins = ((JSONArray) originLayerParser.get("origins"));

                for (Object o : originLayer_origins) {
                    String value = (String) o;
                    String[] valueSplit = value.split(":");
                    originFolder = valueSplit[0];
                    originFileName = valueSplit[1];
                }


                JSONObject originParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator+"data"+File.separator+originFolder+File.separator+"origins"+File.separator+originFileName+".json"));
                ArrayList<String> powersList = (ArrayList<String>) originParser.get("powers");

                for (String string : powersList) {
                    String[] powerLocation = string.split(":");
                    String powerFolder = powerLocation[0];
                    String powerFileName = powerLocation[1];

                    JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator+"data"+File.separator+powerFolder+File.separator+"powers"+File.separator+powerFileName+".json"));
                    powers.add(originFolder+":"+originFileName, fileToHashMap(powerParser), originFolder+":"+originFileName);
                }

                customOrigins.add(new CustomOrigin(originFolder+":"+originFileName, fileToHashMap(originLayerParser), fileToHashMap(originParser), powers));

            } catch (Exception e) {
                e.printStackTrace();
                //Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + datapack.getName() + ". Is it a valid origin file?");
            }
        }
    }

}
