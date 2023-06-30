package me.dueris.genesismc.core.factory;

import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.LayerContainer;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import me.dueris.genesismc.core.utils.FileContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CraftApoli {

    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<LayerContainer> originLayers = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<OriginContainer> originContainers = new ArrayList<>();


    /**
     * @return A copy of each layerTag that is loaded.
     **/
    public static ArrayList<LayerContainer> getLayers() {
        return (ArrayList<LayerContainer>) originLayers.clone();
    }

    /**
     * @return A copy of the CustomOrigin object array for all the origins that are loaded.
     **/
    public static ArrayList<OriginContainer> getOrigins() {
        return (ArrayList<OriginContainer>) originContainers.clone();
    }

    /**
     * @return A copy of The null origin.
     **/
    public static OriginContainer nullOrigin() {
        return new OriginContainer("genesis:origin-null", new FileContainer(new ArrayList<>(List.of("hidden", "origins")), new ArrayList<>(List.of(true, "genesis:origin-null"))) , new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:null", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new PowerContainer("genesis:null", new FileContainer(new ArrayList<>(), new ArrayList<>()), "genesis:origin-null"))));
    }


    /**
     * Parses a JSON file into a HashMap.
     **/
    private static HashMap<String, Object> fileToHashMap(JSONObject JSONFileParser) {
        HashMap<String, Object> data = new HashMap<>();
        for (Object key : JSONFileParser.keySet()) data.put((String) key, JSONFileParser.get(key));
        return data;
    }

    /**
     * Parses a JSON file into a PowerFIleContainer.
     **/
    private static FileContainer fileToFileContainer(JSONObject JSONFileParser) {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        for (Object key : JSONFileParser.keySet()) {
            keys.add((String) key);
            values.add(JSONFileParser.get(key));
        }
        return new FileContainer(keys, values);
    }

    /**
     * Changes the origin names to those specified in the lang file.
     **/
    private static void translateOrigins() {
        for (OriginContainer origin : getCoreOrigins()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                String powerName = Lang.getLocalizedString(power.getName());
                if (powerName != null) power.setName(powerName);
                String powerDescription = Lang.getLocalizedString(power.getDescription());
                if (powerDescription != null) power.setDescription(powerDescription);
            }
        }
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
                    ArrayList<String> originFolder = new ArrayList<>();
                    ArrayList<String> originFileName = new ArrayList<>();

                    for (Path path : files.keySet()) {
                        if (path.equals(Path.of("data" + File.separator + "origins" + File.separator + "origin_layers" + File.separator + "origin.json"))) {
                            originDatapack = true;

                            originLayerParser = (JSONObject) new JSONParser().parse(files.get(path));
                            JSONArray originLayer_origins = ((JSONArray) originLayerParser.get("origins"));

                            for (Object o : originLayer_origins) {
                                String value = (String) o;
                                String[] valueSplit = value.split(":");
                                originFolder.add(valueSplit[0]);
                                originFileName.add(valueSplit[1]);
                            }

                        }
                    }

                    if (!originDatapack) continue;

                    while (originFolder.size() > 0) {

                        for (Path path : files.keySet())
                            if (path.equals(Path.of("data" + File.separator + originFolder.get(0) + File.separator + "origins" + File.separator + originFileName.get(0) + ".json"))) {
                                JSONObject originParser = (JSONObject) new JSONParser().parse(files.get(path));
                                ArrayList<String> powersList = (ArrayList<String>) originParser.get("powers");


                                ArrayList<PowerContainer> powerContainers = new ArrayList<>();

                                if (powersList != null) {
                                    for (String string : powersList) {
                                        String[] powerLocation = string.split(":");
                                        String powerFolder = powerLocation[0];
                                        String powerFileName = powerLocation[1];

                                        try {
                                            JSONObject powerParser = (JSONObject) new JSONParser().parse(files.get(Path.of("data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")));
                                            powerContainers.add(new PowerContainer(powerFolder + ":" + powerFileName, fileToFileContainer(powerParser), originFolder.get(0) + ":" + originFileName.get(0)));
                                        } catch (NullPointerException nullPointerException) {
                                            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \"" + powerFolder + ":" + powerFileName + "\" for \"" + originFolder.get(0) + ":" + originFileName.get(0) + "\"").color(TextColor.color(255, 0, 0)));
                                        }
                                    }
                                }

                                originContainers.add(new OriginContainer(originFolder.get(0) + ":" + originFileName.get(0), fileToFileContainer(originLayerParser), fileToHashMap(originParser), powerContainers));

                            }
                        originFolder.remove(0);
                        originFileName.remove(0);
                    }

                    zip.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (datapack.isFile()) continue;
            }

            //non zip
            File dataDir = new File(datapack.getAbsolutePath() + File.separator + "data");
            if (!dataDir.isDirectory()) continue;
            File origin_layer = null;

            //find layer file
            for (File namespace : dataDir.listFiles()) {
                if (!namespace.isDirectory()) continue;
                String layerNamespace = namespace.getName();
                File originLayers = new File(namespace.getAbsolutePath() + File.separator + "origin_layers");
                if (!originLayers.isDirectory()) continue;
                for (File originLayer : originLayers.listFiles()) {
                    if (!FilenameUtils.getExtension(originLayer.getName()).equals("json")) continue;
                    String layerName = FilenameUtils.getBaseName(originLayer.getName());
                    try {
                        LayerContainer layer = new LayerContainer(layerNamespace+":"+layerName, fileToFileContainer((JSONObject) new JSONParser().parse(new FileReader(originLayer))));
                        //removes an origin layer if a layer with teh same namespace has the replace key set to true
                        if (layer.getReplace() && layerExists(layer)) CraftApoli.originLayers.removeIf(existingLayer -> layer.getTag().equals(existingLayer.getTag()));
                        CraftApoli.originLayers.add(layer);
                        origin_layer = new File(datapack.getName() + File.separator + "data" + File.separator + namespace.getName() + File.separator + "origin_layers" + File.separator + layerName + ".json");
                    } catch (Exception e) {
                        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \""+ datapack.getName() + File.separator + "data" + File.separator + namespace.getName() + File.separator + "origin_layers" + File.separator + layerName + ".json" + "\"").color(TextColor.color(255, 0, 0)));
                    }
                }
            }

            if (origin_layer == null) continue;

            ArrayList<String> originFolder = new ArrayList<>();
            ArrayList<String> originFileName = new ArrayList<>();

            try {
                JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + ".." + File.separator + ".." + File.separator + Bukkit.getServer().getWorlds().get(0).getName() + File.separator + "datapacks"+ File.separator + origin_layer.getPath()));
                JSONArray originLayer_origins = ((JSONArray) originLayerParser.get("origins"));

                for (Object o : originLayer_origins) {
                    String value = (String) o;
                    String[] valueSplit = value.split(":");
                    originFolder.add(valueSplit[0]);
                    originFileName.add(valueSplit[1]);
                }


                while (originFolder.size() > 0) {

                    try {
                        JSONObject originParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + originFolder.get(0) + File.separator + "origins" + File.separator + originFileName.get(0) + ".json"));
                        ArrayList<String> powersList = (ArrayList<String>) originParser.get("powers");

                        ArrayList<PowerContainer> powerContainers = new ArrayList<>();

                        if (powersList != null) {
                            for (String string : powersList) {
                                String[] powerLocation = string.split(":");
                                String powerFolder = powerLocation[0];
                                String powerFileName = powerLocation[1];

                                try {
                                    JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json"));
                                    powerContainers.add(new PowerContainer(powerFolder + ":" + powerFileName, fileToFileContainer(powerParser), originFolder.get(0) + ":" + originFileName.get(0)));
                                } catch (FileNotFoundException fileNotFoundException) {
                                    Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \"" + powerFolder + ":" + powerFileName + "\" for \"" + originFolder.get(0) + ":" + originFileName.get(0) + "\"").color(TextColor.color(255, 0, 0)));
                                }
                            }
                        }

                        originContainers.add(new OriginContainer(originFolder.get(0) + ":" + originFileName.get(0), fileToFileContainer(originLayerParser), fileToHashMap(originParser), powerContainers));

                    } catch (FileNotFoundException fileNotFoundException) {
                        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \"" + datapack.getName() + File.separator + "data" + File.separator + originFolder.get(0) + File.separator + "origins" + File.separator + originFileName.get(0) + ".json" + "\"").color(TextColor.color(255, 0, 0)));
                    }
                    originFolder.remove(0);
                    originFileName.remove(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + datapack.getName() + ". Is it a valid origin file?");
            }
        }
        translateOrigins();
    }

    /**
     * @return The origin that has the specified tag.
     **/
    public static OriginContainer getOrigin(String originTag) {
        for (OriginContainer origin : getOrigins()) if (origin.getTag().equals(originTag)) return origin;
        return null;
    }

    /**
     * @return An ArrayList of all loaded originTags.
     **/
    public static ArrayList<String> getOriginTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (OriginContainer origin : getOrigins()) tags.add(origin.getTag());
        return tags;
    }

    /**
     * @return The HashMap serialized into a byte array.
     **/
    public static byte[] toByteArray(HashMap<LayerContainer, OriginContainer> origin) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(origin);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            Bukkit.getLogger().warning("CRUCIAL ERROR, PLEASE REPORTING THIS IMMEDIATELY TO THE DEVS!!");
            e.printStackTrace();
            return toByteArray(new HashMap<>(Map.of(CraftApoli.getLayers().get(0), CraftApoli.nullOrigin())));
        }
    }

    /**
     * @return The byte array deserialized into the origin specified by the layer.
     **/
    public static OriginContainer toOriginContainer(byte[] origin, LayerContainer originLayer) {
        ByteArrayInputStream bis = new ByteArrayInputStream(origin);
        try {
            ObjectInput oi = new ObjectInputStream(bis);
            HashMap<LayerContainer, OriginContainer> originData = (HashMap<LayerContainer, OriginContainer>) oi.readObject();
            for (LayerContainer layer : originData.keySet()) {
                if (layer.getTag().equals(originLayer.getTag())) return originData.get(layer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return nullOrigin();
        }
        Bukkit.getLogger().warning("CRUCIAL ERROR, PLEASE REPORTING THIS IMMEDIATELY TO THE DEVS!!");
        return nullOrigin();
    }

    /**
     * @return The byte array deserialized into a HashMap of the originLayer and the OriginContainer.
     **/
    public static HashMap<LayerContainer, OriginContainer> toOriginContainer(byte[] origin) {
        ByteArrayInputStream bis = new ByteArrayInputStream(origin);
        try {
            ObjectInput oi = new ObjectInputStream(bis);
            return (HashMap<LayerContainer, OriginContainer>) oi.readObject();
        } catch (Exception e) {
            Bukkit.getLogger().warning("CRUCIAL ERROR, PLEASE REPORTING THIS IMMEDIATELY TO THE DEVS!!");
            e.printStackTrace();
            return new HashMap<>(Map.of(CraftApoli.getLayers().get(0), CraftApoli.nullOrigin()));
        }
    }

    /**
     * @return True if an origin is part of the core origins.
     **/
    public static Boolean isCoreOrigin(OriginContainer origin) {
        return origin.getTag().equals("origins:arachnid") || origin.getTag().equals("origins:avian")
                || origin.getTag().equals("origins:blazeborn")
                || origin.getTag().equals("origins:elytrian")
                || origin.getTag().equals("origins:enderian")
                || origin.getTag().equals("origins:feline")
                || origin.getTag().equals("origins:human")
                || origin.getTag().equals("origins:merling")
                || origin.getTag().equals("origins:phantom")
                || origin.getTag().equals("origins:shulk")
                || origin.getTag().equals("origins:allay")
                || origin.getTag().equals("origins:bee")
                || origin.getTag().equals("origins:creep")
                || origin.getTag().equals("origins:piglin")
                || origin.getTag().equals("origins:rabbit")
                || origin.getTag().equals("origins:sculkling")
                || origin.getTag().equals("origins:slimeling")
                || origin.getTag().equals("origins:starborne");
    }

    /**
     * @return An ArrayList of loaded core origins.
     **/
    public static ArrayList<OriginContainer> getCoreOrigins() {
        ArrayList<OriginContainer> coreOrigins = new ArrayList<>();
        for (OriginContainer origin : getOrigins()) {
            if (isCoreOrigin(origin)) coreOrigins.add(origin);
        }
        return coreOrigins;
    }

    /**
     * @return The loaded layer with the specified tag. If there is no layer with that tag the first layer will be returned.
     **/
    public static LayerContainer getLayerFromTag(String layerTag) {
        for (LayerContainer layer : CraftApoli.getLayers()) {
            if (layer.getTag().equals(layerTag)) return layer;
        }
        return CraftApoli.getLayers().get(0);
    }

    /**
     * @return True if the layer given is currently loaded.
     **/
    public static boolean layerExists(LayerContainer layer) {
        for (LayerContainer layers : CraftApoli.getLayers()) {
            if (layers.getTag().equals(layer.getTag())) return true;
        }
        return false;
    }

}
