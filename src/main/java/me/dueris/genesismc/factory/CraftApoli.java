package me.dueris.genesismc.factory;

import io.netty.util.internal.ConcurrentSet;
import me.dueris.genesismc.Bootstrap;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CraftApoli {

    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<LayerContainer> originLayers = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<OriginContainer> originContainers = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<PowerContainer> powerContainers = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    public static ConcurrentHashMap<String, PowerContainer> keyedPowerContainers = new ConcurrentHashMap();

    /**
     * @return A copy of each layerTag that is loaded.
     **/
    public static ArrayList<LayerContainer> getLayers() {
        return originLayers;
    }

    /**
     * @return A copy of the CustomOrigin object array for all the origins that are loaded.
     **/
    public static ArrayList<OriginContainer> getOrigins() {
        return originContainers;
    }

    public static OriginContainer getOrigins(String tag) {
        for(OriginContainer origin : getOrigins()){
            if(origin.getTag().equals(tag)) return origin;
        }
        return null;
    }

    public static ArrayList<PowerContainer> getPowers() {
        return powerContainers;
    }

    public static PowerContainer getPowerContainerFromTag(String tag){
        return keyedPowerContainers.get(tag);
    }

    /**
     * @return A copy of The null origin.
     **/
    public static OriginContainer nullOrigin() {
        return new OriginContainer(new NamespacedKey("genesis", "origin-null"), new FileContainer(new ArrayList<>(List.of("hidden", "origins")), new ArrayList<>(List.of(true, "genesis:origin-null"))), new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:null", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new PowerContainer(new NamespacedKey("genesis", "null"), new FileContainer(new ArrayList<>(), new ArrayList<>()), null, false))));
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
    public static FileContainer fileToFileContainer(JSONObject JSONFileParser) {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        for (Object key : JSONFileParser.keySet()) {
            keys.add((String) key);
            if(JSONFileParser.get(key).toString().startsWith("apoli:")){
                values.add(JSONFileParser.get(key).toString().replace("apoli:", "origins:"));
            }else{
                values.add(JSONFileParser.get(key));
            }
        }
        return new FileContainer(keys, values);
    }

    /**
     * Changes the origin names to those specified in the lang file.
     **/
    private static void translateOrigins() {
        for (OriginContainer origin : getCoreOrigins()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                String powerName = power.getName();
                if (powerName != null) power.setName(powerName);
                String powerDescription = power.getDescription();
                if (powerDescription != null) power.setDescription(powerDescription);
            }
        }
    }

    public static void processNestedPowers(PowerContainer powerContainer, ArrayList<PowerContainer> powerContainers, String powerFolder, String powerFileName) {
        ArrayList<PowerContainer> newPowerContainers = new ArrayList<>();

        for (String key : powerContainer.getPowerFile().getKeys()) {
            Object subPowerValue = powerContainer.getPowerFile().get(key);

            if (subPowerValue instanceof JSONObject subPowerJson) {
                FileContainer subPowerFile = fileToFileContainer(subPowerJson);

                PowerContainer newPower = new PowerContainer(new NamespacedKey(powerFolder, powerFileName + "_" + key), subPowerFile, subPowerJson.toJSONString().split("\n"), true, false);
                powerContainers.add(newPower);
                keyedPowerContainers.put(powerFolder + ":" + powerFileName + "_" + key, newPower);
                newPowerContainers.add(newPower);
            }
        }
        powerContainers.addAll(newPowerContainers);
    }

    public static ArrayList<PowerContainer> getNestedPowers(PowerContainer power){
        ArrayList<PowerContainer> nested = new ArrayList<>();
        if(power == null) return nested;
        String powerFolder = power.getTag().split(":")[0];
        String powerFileName = power.getTag().split(":")[1];

        for (String key : power.getPowerFile().getKeys()) {
            if(keyedPowerContainers.get(new NamespacedKey(powerFolder, powerFileName).asString() + "_" + key) != null){
                nested.add(keyedPowerContainers.get(powerFolder + ":" + powerFileName + "_" + key));
            }
        }
        return nested;
    }

    public static File datapackDir() {
        return new File(MinecraftServer.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath().toString());
    }

    public static File[] datapacksInDir() {
        return datapackDir().listFiles();
    }

    private static BufferedReader readZipEntry(ZipFile zip, InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

    /**
     * ArrayList of unzipped files that are scheduled for removal at the end of the parsing process
     */
    public static ArrayList<File> unzippedFiles = new ArrayList<>();

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    public static String getWorldContainerName(){
        return GenesisMC.world_container;
    }

    private static int dynamic_thread_count = 0;

    public static int getDynamicThreadCount(){
        return dynamic_thread_count;
    }

    public static void setupDynamicThreadCount(){
        int avalibleJVMThreads = Runtime.getRuntime().availableProcessors() * 2;
        dynamic_thread_count = avalibleJVMThreads < 4 ? avalibleJVMThreads : avalibleJVMThreads >= GenesisDataFiles.getMainConfig().getInt("max-loader-threads") ? GenesisDataFiles.getMainConfig().getInt("max-loader-threads") : avalibleJVMThreads;
    }

    public static void unzip(File source, String out) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry entry = zis.getNextEntry();
    
            while (entry != null) {
                File file = new File(out, entry.getName());
                if (!entry.getName().endsWith(".jar")){
                    if (entry.isDirectory()) {
                        file.mkdirs();
                    } else {
                        File parent = file.getParentFile();
                        if (!parent.exists()) {
                            parent.mkdirs();
                        }
        
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                            int bufferSize = Math.toIntExact(entry.getSize());
                            byte[] buffer = new byte[bufferSize > 0 ? bufferSize : 1];
                            int location;
        
                            while ((location = zis.read(buffer)) != -1) {
                                bos.write(buffer, 0, location);
                            }
                        }
                    }
                    entry = zis.getNextEntry();
                }
            }
        }
    }

    /**
     * Loads the custom origins from the datapack dir into memory.
     * @throws ExecutionException
     * @throws InterruptedException
     **/
    public static void loadOrigins() throws InterruptedException, ExecutionException {
        Boolean showErrors = Boolean.valueOf(GenesisDataFiles.getMainConfig().get("console-print-parse-errors").toString());
        File DatapackDir = new File(MinecraftServer.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath().toString());
        File[] datapacks = DatapackDir.listFiles();
        if (datapacks == null) return;

        Void future = CompletableFuture.runAsync(() -> {
            for (File datapack : datapacks){
                if (FilenameUtils.getExtension(datapack.getName()).equals(".zip") || FilenameUtils.getExtension(datapack.getName()).equals("zip")) {
                    try {
                        unzip(datapack, GenesisMC.getTmpFolder().getAbsolutePath() + File.separator + datapack.getName().replace(".zip", ""));
                        unzippedFiles.add(Path.of(GenesisMC.getTmpFolder().getAbsolutePath() + File.separator + datapack.getName().replace(".zip", "")).toFile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            List<File> datapacksToParse = new ArrayList();
            for (File packInDatapacks : DatapackDir.listFiles()) {datapacksToParse.add(packInDatapacks);}
            datapacksToParse.addAll(unzippedFiles);
            for (File datapack : datapacksToParse) {
                try {
                    CompletableFuture.runAsync(() -> {
                        File dataDir = new File(datapack.getAbsolutePath() + File.separator + "data");
                        if (!dataDir.isDirectory()) return;
                        File origin_layer = null;

                        //find layer file
                        for (File namespace : dataDir.listFiles()) {
                            if (!namespace.isDirectory()) continue;
                            for(File powerDir : namespace.listFiles()){
                                if(powerDir.getName().equals("powers") && powerDir.isDirectory()){
                                    for(File powerFile : powerDir.listFiles()){
                                        try {
                                            if(!powerFile.isDirectory()){
                                                String powerFolder = namespace.getName();
                                                String powerFileName = powerFile.getName().replace(".json", "");

                                                JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json"));
                                                if (powerParser.containsKey("type") && "origins:multiple".equals(powerParser.get("type"))) {
                                                    PowerContainer powerContainer = new PowerContainer(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")), false, true);
                                                    powerContainers.add(powerContainer);
                                                    keyedPowerContainers.put(powerFolder + ":" + powerFileName, powerContainer);
                                                    processNestedPowers(powerContainer, new ArrayList<>(), powerFolder, powerFileName);
                                                } else {
                                                    PowerContainer power = new PowerContainer(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")), false);
                                                    powerContainers.add(power);
                                                    keyedPowerContainers.put(powerFolder + ":" + powerFileName, power);
                                                }

                                            }
                                        } catch (FileNotFoundException fileNotFoundException) {
                                            if (showErrors)
                                                System.err.println("[GenesisMC] Error parsing \"%powerFolder%:%powerFileName%\"".replace("%powerFolder", namespace.getName()).replace("%powerFileName", powerFile.getName()));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                            String layerNamespace = namespace.getName();
                            File originLayers = new File(namespace.getAbsolutePath() + File.separator + "origin_layers");
                            if (!originLayers.isDirectory()) continue;
                            for (File originLayer : originLayers.listFiles()) {
                                if (!FilenameUtils.getExtension(originLayer.getName()).equals("json")) continue;
                                String layerName = FilenameUtils.getBaseName(originLayer.getName());
                                try {
                                    LayerContainer layer = new LayerContainer(new NamespacedKey(layerNamespace, layerName), fileToFileContainer((JSONObject) new JSONParser().parse(new FileReader(originLayer))));

                                    if (layer.getReplace() && layerExists(layer)) {
                                        //removes an origin layer if a layer with the same namespace has the replace key set to true
                                        CraftApoli.originLayers.removeIf(existingLayer -> layer.getTag().equals(existingLayer.getTag()));
                                        CraftApoli.originLayers.add(layer);
                                    } else if (layerExists(layer)) {
                                        //adds an origin to a layer if it already exists and the replace key is null or false
                                        LayerContainer existingLayer = CraftApoli.originLayers.get(CraftApoli.originLayers.indexOf(getLayerFromTag(layer.getTag())));
                                        existingLayer.addOrigin(layer.getOrigins());
                                        CraftApoli.originLayers.set(CraftApoli.originLayers.indexOf(getLayerFromTag(layer.getTag())), existingLayer);
                                    } else {
                                        CraftApoli.originLayers.add(layer);
                                    }

                                    origin_layer = new File(datapack.getName() + File.separator + "data" + File.separator + namespace.getName() + File.separator + "origin_layers" + File.separator + layerName + ".json");
                                } catch (Exception e) {
                                    if (showErrors) {
                                        System.err.println("[GenesisMC] Error parsing \"%datapack%%sep%data%sep%%namespace%%sep%origin_layers%sep%%layerName%.json\"".replace("%datapack%", datapack.getName()).replace("%sep%", File.separator).replace("%namespace%", namespace.getName()).replace("%layerName%", layerName));
                                    }
                                }
                            }
                        }

                        if (origin_layer == null) return;

                        //sets up arrays for origins in the datapack
                        ArrayList<String> originFolder = new ArrayList<>();
                        ArrayList<String> originFileName = new ArrayList<>();

                        try {
                            JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + origin_layer.getPath().split(datapack.getName())[1]));
                            JSONArray originLayer_origins = ((JSONArray) originLayerParser.get("origins"));

                            //gets every origin from the layer
                            for (Object o : originLayer_origins) {
                                String value = (String) o;
                                String[] valueSplit = value.split(":");
                                originFolder.add(valueSplit[0]);
                                originFileName.add(valueSplit[1]);
                            }

                            //gets the powers for every origin
                            while (originFolder.size() > 0) {

                                try {
                                    JSONObject originParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + originFolder.get(0) + File.separator + "origins" + File.separator + originFileName.get(0) + ".json"));
                                    ArrayList<String> powersList = (ArrayList<String>) originParser.get("powers");

                                    ArrayList<PowerContainer> powerContainers = new ArrayList<>();

                                    if (powersList != null) {
                                        for (String string : powersList) {
                                            boolean finished = false;
                                            if(keyedPowerContainers.containsKey(string)){
                                                powerContainers.add(keyedPowerContainers.get(string));
                                                finished = true;
                                            }
                                            for(PowerContainer power : getNestedPowers(keyedPowerContainers.get(string))){
                                                if(power != null){
                                                    powerContainers.add(power);
                                                    finished = true;
                                                }
                                            }
                                            if(!finished){
                                                // Not found in database, probably an error, move to backup parse to ensure all powers are added
                                                String[] powerLocation = string.split(":");
                                                String powerFolder = powerLocation[0];
                                                String powerFileName = powerLocation[1];

                                                try {
                                                    JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json"));
                                                    if (powerParser.containsKey("type") && "origins:multiple".equals(powerParser.get("type"))) {
                                                        PowerContainer powerContainer = new PowerContainer(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")), false, true);
                                                        powerContainers.add(powerContainer);
                                                        processNestedPowers(powerContainer, powerContainers, powerFolder, powerFileName);
                                                    } else {
                                                        powerContainers.add(new PowerContainer(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")), false));
                                                    }
                                                } catch (FileNotFoundException fileNotFoundException) {
                                                    if (showErrors)
                                                        System.err.println("[GenesisMC] Error parsing \"%powerFolder%:%powerFileName%\" for \"%originFolder%:%originFileName%\"".replace("%powerFolder", powerFolder).replace("%powerFileName", powerFileName).replace("%originFolder%", originFolder.get(0)).replace("%originFileName%", originFileName.get(0)));
                                                }
                                            }
                                        }
                                    }
                                    OriginContainer origin = new OriginContainer(new NamespacedKey(originFolder.get(0), originFileName.get(0)), fileToFileContainer(originLayerParser), fileToHashMap(originParser), powerContainers);
                                    originContainers.add(origin);

                                } catch (FileNotFoundException fileNotFoundException) {
                                    if (showErrors)
                                        //Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \"" + datapack.getName() + File.separator + "data" + File.separator + originFolder.get(0) + File.separator + "origins" + File.separator + originFileName.get(0) + ".json" + "\"").color(TextColor.color(255, 0, 0)));
                                        System.err.println("[GenesisMC] Error parsing \"%datapack%%sep%data%sep%%originFolder%%sep%origins%sep%%originFileName%.json\"".replace("%datapack%", datapack.getName()).replace("%originFolder", originFolder.get(0)).replace("%sep%", File.separator).replace("%originFileName%", originFileName.get(0)));
                                }
                                originFolder.remove(0);
                                originFileName.remove(0);
                            }
                        } catch (Exception e) {
                            if (showErrors)
                                e.printStackTrace();
                            //Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Failed to parse the \"/data/origins/origin_layers/origin.json\" file for " + datapack.getName() + ". Is it a valid origin file?");
                        }
                    }, GenesisMC.loaderThreadPool).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).thenRun(() -> {
            //if an origin is a core one checks if there are translations for the powers
            translateOrigins();
            TagRegistry.runParse();
        }).get();
    }


    /**
     * @return The origin that has the specified tag.
     **/
    public static OriginContainer getOrigin(String originTag) {
        for (OriginContainer origin : getOrigins()) if (origin.getTag().equals(originTag)) return origin;
        return nullOrigin();
    }

    public static void unloadData() {
        originContainers.clear();
        originLayers.clear();
        getOrigins().clear();
        getCoreOrigins().clear();
        getOriginTags().clear();
        getLayers().clear();
        getPowers().clear();
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
    public static String toSaveFormat(HashMap<LayerContainer, OriginContainer> origin, Player p) {
        StringBuilder data = new StringBuilder();
        for (LayerContainer layer : origin.keySet()) {
            OriginContainer layerOrigins = origin.get(layer);
            ArrayList<String> powers = new ArrayList<>();
            for(PowerContainer power : OriginPlayerUtils.powerContainer.get(p).get(layer)){
                powers.add(power.getTag());
            }
            int powerSize = 0;
            if (powers != null) powerSize = powers.size();
            data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);
            if (powers != null) for (String power : powers) data.append("|").append(power);
            data.append("\n");
        }
//        System.out.println(data.toString());
        return data.toString();
    }

    /**
     * @return The HashMap serialized into a byte array.
     **/
    public static String toOriginSetSaveFormat(HashMap<LayerContainer, OriginContainer> origin) {
        StringBuilder data = new StringBuilder();
        for (LayerContainer layer : origin.keySet()) {
            OriginContainer layerOrigins = origin.get(layer);
            ArrayList<String> powers = layerOrigins.getPowers();
            int powerSize = 0;
            if (powers != null) powerSize = powers.size();
            data.append(layer.getTag()).append("|").append(layerOrigins.getTag()).append("|").append(powerSize);
            if (powers != null) for (String power : powers) data.append("|").append(power);
            data.append("\n");
        }
//        System.out.println(data.toString());
        return data.toString();
    }

    /**
     * @return The byte array deserialized into the origin specified by the layer.
     **/
    public static OriginContainer toOrigin(String originData, LayerContainer originLayer) {
        if (originData != null) {
            try{
                String[] layers = originData.split("\n");
                for (String layer : layers) {
                    String[] layerData = layer.split("\\|");
                    if (CraftApoli.getLayerFromTag(layerData[0]).equals(originLayer)) {
                        return CraftApoli.getOrigin(layerData[1]);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                return CraftApoli.nullOrigin();
            }
        }
        return CraftApoli.nullOrigin();
    }

    /**
     * @return The byte array deserialized into a HashMap of the originLayer and the OriginContainer.
     **/
    public static HashMap<LayerContainer, OriginContainer> toOrigin(String originData) {
        HashMap<LayerContainer, OriginContainer> containedOrigins = new HashMap<>();
        if (originData == null) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                containedOrigins.put(layer, CraftApoli.nullOrigin());
            }
        } else {
            try{
                String[] layers = originData.split("\n");
                for (String layer : layers) {
                    String[] layerData = layer.split("\\|");
                    LayerContainer layerContainer = CraftApoli.getLayerFromTag(layerData[0]);
                    OriginContainer originContainer = CraftApoli.getOrigin(layerData[1]);
                    containedOrigins.put(layerContainer, originContainer);
                }
            }catch(Exception e){
                e.printStackTrace();
                for(LayerContainer layer : CraftApoli.getLayers()){
                    containedOrigins.put(layer, CraftApoli.nullOrigin());
                }
                return containedOrigins;
            }
        }
        return containedOrigins;
    }

    /**
     * @return True if an origin is part of the core origins.
     **/
    public static Boolean isCoreOrigin(OriginContainer origin) {
        return origin.getTag().equals("origins:arachnid") 
                || origin.getTag().equals("origins:avian")
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
        for (LayerContainer loadedLayers : CraftApoli.getLayers()) {
            if (loadedLayers.getTag().equals(layer.getTag())) return true;
        }
        return false;
    }

}
