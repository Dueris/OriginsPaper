package me.dueris.genesismc.factory;

import io.netty.util.internal.ConcurrentSet;
import me.clip.placeholderapi.libs.kyori.adventure.key.Namespaced;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.events.OriginLoadEvent;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.*;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import oshi.hardware.VirtualMemory;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.destroystokyo.paper.NamespacedTag;

import java.beans.JavaBean;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CraftApoli {

    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<LayerContainer> originLayers = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private static ConcurrentSet<OriginContainer> originContainers = new ConcurrentSet<>();
    @SuppressWarnings("FieldMayBeFinal")
    private static ConcurrentSet<PowerContainer> powerContainers = new ConcurrentSet<>();


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
        List<OriginContainer> originContainersD = new ArrayList<>(originContainers);
        return (ArrayList<OriginContainer>) originContainersD;
    }

    public static OriginContainer getOrigins(String tag) {
        for(OriginContainer origin : getOrigins()){
            if(origin.getTag().equals(tag)) return origin;
        }
        return null;
    }

    public static ArrayList<PowerContainer> getPowers() {
        List<PowerContainer> d = new ArrayList<>(powerContainers);
        return (ArrayList<PowerContainer>) d;
    }

    /**
     * @return A copy of The null origin.
     **/
    public static OriginContainer nullOrigin() {
        return new OriginContainer(new NamespacedKey("genesis", "origin-null"), new FileContainer(new ArrayList<>(List.of("hidden", "origins")), new ArrayList<>(List.of(true, "genesis:origin-null"))), new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:null", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new PowerContainer(new NamespacedKey("genesis", "null"), new FileContainer(new ArrayList<>(), new ArrayList<>()), "genesis:origin-null"))));
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
                String source = powerContainer.getSource();

                PowerContainer newPower = new PowerContainer(new NamespacedKey(powerFolder, powerFileName + "_" + key), subPowerFile, source, true);
                powerContainers.add(newPower);
                newPowerContainers.add(newPower);
            }
        }
        powerContainers.addAll(newPowerContainers);
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
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if(Path.of(filePath).toFile().exists()) return;
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        }
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

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

    /**
     * Loads the custom origins from the datapack dir into memory.
     **/
    public static void loadOrigins() {
        Boolean showErrors = Boolean.valueOf(GenesisDataFiles.getMainConfig().get("console-print-parse-errors").toString());
        File DatapackDir = new File(MinecraftServer.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath().toString());
        File[] datapacks = DatapackDir.listFiles();
        if (datapacks == null) return;

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (File datapack : datapacks){
                if (FilenameUtils.getExtension(datapack.getName()).equals(".zip") || FilenameUtils.getExtension(datapack.getName()).equals("zip")) {
                    try {
                        CraftApoli.unzip(datapack.getAbsolutePath(), DatapackDir.getAbsolutePath() + File.separator + datapack.getName().replace(".zip", "") + "unzipped");
                        File dp = new File(DatapackDir.getAbsolutePath() + File.separator + datapack.getName().replace(".zip", "") + "unzipped");
                        File dataDir = new File(dp.getAbsolutePath() + File.separator + "data");
                        if (!dataDir.isDirectory()) continue;
                        File origin_layer = null;
                        unzippedFiles.add(dp);
    
                    } catch (Exception e) {
                        //yeah imma fail this silently
                    }
                } else {
                    if (datapack.isFile()) continue;
                }
            }
        }).thenRun(() -> {
            for (File datapack : datapacks) {
                GenesisMC.loaderThreadPool.submit(() -> {
                    File dataDir = new File(datapack.getAbsolutePath() + File.separator + "data");
                    if (!dataDir.isDirectory()) return;
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
                        JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(datapackDir().getAbsolutePath() + File.separator + origin_layer.getPath()));
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
                                        String[] powerLocation = string.split(":");
                                        String powerFolder = powerLocation[0];
                                        String powerFileName = powerLocation[1];
        
                                        try {
                                            JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json"));
                                            if (powerParser.containsKey("type") && "origins:multiple".equals(powerParser.get("type"))) {
                                                PowerContainer powerContainer = new PowerContainer(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), originFolder.get(0) + ":" + originFileName.get(0));
                                                powerContainers.add(powerContainer);
                                                processNestedPowers(powerContainer, powerContainers, powerFolder, powerFileName);
                                            } else {
                                                powerContainers.add(new PowerContainer(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), originFolder.get(0) + ":" + originFileName.get(0)));
                                            }
                                        } catch (FileNotFoundException fileNotFoundException) {
                                            if (showErrors)
                                                System.err.println("[GenesisMC] Error parsing \"%powerFolder%:%powerFileName%\" for \"%originFolder%:%originFileName%\"".replace("%powerFolder", powerFolder).replace("%powerFileName", powerFileName).replace("%originFolder%", originFolder.get(0)).replace("%originFileName%", originFileName.get(0)));
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
                });
            }
        }).thenRun(() -> {
            //if an origin is a core one checks if there are translations for the powers
            translateOrigins();
            TagRegistry.runParse();
        });
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
    public static String toSaveFormat(HashMap<LayerContainer, OriginContainer> origin) {
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
        System.out.println(data.toString());
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
