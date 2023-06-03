package me.dueris.genesismc.core.factory;

import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import me.dueris.genesismc.core.utils.PowerFileContainer;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.nio.file.*;
//import java.nio.file;

public class CraftApoliRewriten {

    private static final OriginContainer null_Origin = new OriginContainer("genesis:origin-null", new HashMap<String, Object>(Map.of( "hidden", true, "origins", "genesis:origin-null")), new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:null", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new PowerContainer("genesis:null", new PowerFileContainer(new ArrayList<>(), new ArrayList<>()), "genesis:origin-null"))));

    public static OriginContainer nullOrigin() {
        return null_Origin;
    }

    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<OriginContainer> originContainers = new ArrayList<>();

    /**
     * @return A copy of the CustomOrigin object array for all the origins that are loaded.
     **/
    public static ArrayList<OriginContainer> getOrigins() {
        return (ArrayList<OriginContainer>) originContainers.clone();
    }

    private static HashMap<String, Object> fileToHashMap(JSONObject JSONFileParser) {
        HashMap<String, Object> data = new HashMap<>();
        for (Object key : JSONFileParser.keySet()) data.put((String) key, JSONFileParser.get(key));
        return data;
    }

    private static PowerFileContainer fileToPowerFileContainer(JSONObject JSONFileParser) {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        for (Object key : JSONFileParser.keySet()) {
            keys.add((String) key);
            values.add(JSONFileParser.get((String) key));
        }
        return new PowerFileContainer(keys, values);
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

                            ArrayList<PowerContainer> powerContainers= new ArrayList<>();

                            for (String string : powersList) {
                                String[] powerLocation = string.split(":");
                                String powerFolder = powerLocation[0];
                                String powerFileName = powerLocation[1];

                                JSONObject powerParser = (JSONObject) new JSONParser().parse(files.get(Path.of("data"+File.separator+powerFolder+File.separator+"powers"+File.separator+powerFileName+".json")));
                                powerContainers.add(new PowerContainer(powerFolder+":"+powerFileName, fileToPowerFileContainer(powerParser), powerFolder+":"+powerFileName));
                            }

                            originContainers.add(new OriginContainer(originFolder+":"+originFileName, fileToHashMap(originLayerParser), fileToHashMap(originParser), powerContainers));
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

                ArrayList<PowerContainer> powerContainers= new ArrayList<>();

                for (String string : powersList) {
                    String[] powerLocation = string.split(":");
                    String powerFolder = powerLocation[0];
                    String powerFileName = powerLocation[1];

                    JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator+"data"+File.separator+powerFolder+File.separator+"powers"+File.separator+powerFileName+".json"));
                    powerContainers.add(new PowerContainer(powerFolder+":"+powerFileName, fileToPowerFileContainer(powerParser), powerFolder+":"+powerFileName));
                }

                originContainers.add(new OriginContainer(originFolder+":"+originFileName, fileToHashMap(originLayerParser), fileToHashMap(originParser), powerContainers));

            } catch (Exception e) {
                e.printStackTrace();
                //Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + datapack.getName() + ". Is it a valid origin file?");
            }
        }
    }

    public static OriginContainer getOrigin(String originTag) {
        for (OriginContainer origin : getOrigins()) if (origin.getTag().equals(originTag)) return origin;
        return null;
    }

    public static ArrayList<String> getOriginTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (OriginContainer origin : getOrigins()) tags.add(origin.getTag());
        return tags;
    }

    public static byte[] toByteArray(OriginContainer origin) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(origin);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public static OriginContainer toOriginContainer(byte[] origin) {
        ByteArrayInputStream bis = new ByteArrayInputStream(origin);
        try {
            ObjectInput oi = new ObjectInputStream(bis);
            return (OriginContainer) oi.readObject();
        } catch (Exception e) {
            return null;
        }
    }

}
