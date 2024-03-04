package me.dueris.genesismc.factory;

import me.dueris.calio.builder.NamespaceRemapper;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.*;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CraftApoli {

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    @SuppressWarnings("FieldMayBeFinal")
    /**
     * ArrayList of unzipped files that are scheduled for removal at the end of the parsing process
     */
    public static ArrayList<File> unzippedFiles = new ArrayList<>();
    private static int dynamic_thread_count = 0;

    public static List<Layer> getLayersFromRegistry(){
        return GenesisMC.getPlugin().registry.retrieve(Registries.LAYER).values().stream().toList();
    }

    public static List<Origin> getOriginsFromRegistry(){
        return ((Registrar<Origin>)GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).values().stream().toList();
    }

    public static List<Power> getPowersFromRegistry(){
        return ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).values().stream().toList();
    }

    public static Origin getOrigin(String originTag) {
        for (Origin o : ((Registrar<Origin>)GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).values()) if (o.getTag().equals(originTag)) return o;
        return nullOrigin();
    }

    public static Layer getLayerFromTag(String layerTag) {
        for (Layer l : ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).values()) if (l.getTag().equals(layerTag)) return l;
        return ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(new NamespacedKey("origins", "origin"));
    }

    /**
     * @return A copy of The null origin.
     **/
    public static Origin nullOrigin() {
        return new Origin(new NamespacedKey("genesis", "origin-null"), new DatapackFile(new ArrayList<>(List.of("hidden", "origins")), new ArrayList<>(List.of(true, "genesis:origin-null"))), new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:null", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new Power(new NamespacedKey("genesis", "null"), new DatapackFile(new ArrayList<>(), new ArrayList<>()), null, false))));
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
    public static DatapackFile fileToFileContainer(JSONObject JSONFileParser) {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        for (Object key : JSONFileParser.keySet()) {
            keys.add((String) key);
            values.add(JSONFileParser.get(key));
        }
        return new DatapackFile(keys, values);
    }

    /**
     * Changes the origin names to those specified in the lang file.
     **/
    private static void translateOrigins() {
        for (Origin origin : ((Registrar<Origin>)GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).values().stream().filter(origin -> isCoreOrigin(origin)).toList()) {
            for (Power power : origin.getPowerContainers()) {
                String powerName = power.getName();
                if (powerName != null) power.setName(powerName);
                String powerDescription = power.getDescription();
                if (powerDescription != null) power.setDescription(powerDescription);
            }
        }
    }

    public static void processNestedPowers(Power powerContainer, ArrayList<Power> powerContainers, String powerFolder, String powerFileName) {
        for (String key : powerContainer.getPowerFile().getKeys()) {
            Object subPowerValue = powerContainer.getPowerFile().get(key);

            if (subPowerValue instanceof JSONObject subPowerJson) {
                DatapackFile subPowerFile = fileToFileContainer(subPowerJson);

                Power newPower = new Power(new NamespacedKey(powerFolder, powerFileName + "_" + key.toLowerCase()), subPowerFile, subPowerJson.toJSONString(), true, false, powerContainer);
                ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).register(newPower);
            }
        }
    }

    public static ArrayList<Power> getNestedPowers(Power power) {
        ArrayList<Power> nested = new ArrayList<>();
        if (power == null) return nested;
        String powerFolder = power.getTag().split(":")[0].toLowerCase();
        String powerFileName = power.getTag().split(":")[1].toLowerCase();

        for (String key : power.getPowerFile().getKeys()) {
            if(power.getObject(key) instanceof JSONObject){
                if (((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(new NamespacedKey(powerFolder, powerFileName + "_" + key.toLowerCase())) != null) {
                    nested.add(((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(powerFolder + ":" + powerFileName + "_" + key.toLowerCase())));
                }
            }
        }
        return nested;
    }

    public static File datapackDir() {
        return new File(GenesisMC.server.getWorldPath(LevelResource.DATAPACK_DIR).toAbsolutePath().toString());
    }

    public static File[] datapacksInDir() {
        return datapackDir().listFiles();
    }

    private static BufferedReader readZipEntry(ZipFile zip, InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

    public static String getWorldContainerName() {
        return GenesisMC.world_container;
    }

    public static int getDynamicThreadCount() {
        return dynamic_thread_count;
    }

    public static void setupDynamicThreadCount() {
        int avalibleJVMThreads = Runtime.getRuntime().availableProcessors() * 2;
        dynamic_thread_count = avalibleJVMThreads < 4 ? avalibleJVMThreads : avalibleJVMThreads >= GenesisConfigs.getMainConfig().getInt("max-loader-threads") ? GenesisConfigs.getMainConfig().getInt("max-loader-threads") : avalibleJVMThreads;
    }

    public static void unzip(File source, String out) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {
                File file = new File(out, entry.getName());
                if (!entry.getName().endsWith(".jar") && !entry.getName().contains("../")) {
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

    public static boolean layerExists(Layer layer) {
        for (Layer loadedLayers : CraftApoli.getLayersFromRegistry()) {
            if (loadedLayers.getTag().equals(layer.getTag())) return true;
        }
        return false;
    }

    /**
     * Loads the custom origins from the datapack dir into memory.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     **/
    public static void loadOrigins(IRegistry registry) throws InterruptedException, ExecutionException {
        if(((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).hasEntries() || ((Registrar<Origin>)GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).hasEntries() || ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).hasEntries()) return; // Already parsed.
        boolean showErrors = Boolean.valueOf(GenesisConfigs.getMainConfig().get("console-print-parse-errors").toString());
        List<File> datapacks = new ArrayList();
        ((Registrar<DatapackRepository>)registry.retrieve(Registries.PACK_SOURCE)).forEach((k, l) -> {
            for(File file : l.getPath().toFile().listFiles()){
                datapacks.add(file);
            }
        });
        if (datapacks == null || datapacks.isEmpty()) return;

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (File datapack : datapacks) {
                if (FilenameUtils.getExtension(datapack.getName()).equals(".zip") || FilenameUtils.getExtension(datapack.getName()).equals("zip")) {
                    try {
                        unzip(datapack, GenesisMC.getTmpFolder().getAbsolutePath() + File.separator + datapack.getName().replace(".zip", ""));
                        unzippedFiles.add(Path.of(GenesisMC.getTmpFolder().getAbsolutePath() + File.separator + datapack.getName().replace(".zip", "")).toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            List<File> datapacksToParse = new ArrayList();
            datapacksToParse.addAll(datapacks);
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
                            for (File powerDir : namespace.listFiles()) {
                                if (powerDir.getName().equals("powers") && powerDir.isDirectory()) {
                                    for (File powerFile : powerDir.listFiles()) {
                                        try {
                                            if (!powerFile.isDirectory()) {
                                                JsonValidator.validateJsonFile(powerFile.getAbsolutePath());
                                                String powerFolder = namespace.getName().toLowerCase();
                                                String powerFileName = powerFile.getName().replace(".json", "").toLowerCase();

                                                JSONObject powerParser = NamespaceRemapper.createRemapped(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json"));
                                                if (powerParser.containsKey("type") && "apoli:multiple".equals(powerParser.get("type"))) {
                                                    Power powerContainer = new Power(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(powerFile), false, true);
                                                    ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).register(powerContainer);
                                                    processNestedPowers(powerContainer, new ArrayList<>(), powerFolder, powerFileName);
                                                } else {
                                                    Power power = new Power(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(powerFile), false);
                                                    ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).register(power);
                                                }

                                            }
                                        } catch (Exception ee) {
                                            ee.printStackTrace();
                                            if (showErrors)
                                                System.err.println("[GenesisMC] Error parsing \"%powerFolder%:%powerFileName%\"".replace("%powerFolder%", namespace.getName()).replace("%powerFileName%", powerFile.getName()));
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
                                    Layer layer = new Layer(new NamespacedKey(layerNamespace, layerName), fileToFileContainer((JSONObject) new JSONParser().parse(new FileReader(originLayer))));

                                    if (layer.getReplace() && layerExists(layer)) {
                                        //removes an origin layer if a layer with the same namespace has the replace key set to true
                                        AtomicBoolean r = new AtomicBoolean(false);
                                        ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).forEach((k, l) -> {
                                            if(!r.get() && l.getTag().equals(layer.getTag())){
                                                r.set(true);
                                            }
                                        });
                                        if(r.get()){
                                            ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).removeFromRegistry(NamespacedKey.fromString(layer.getTag()));
                                        }
                                        ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).register(layer);
                                    } else if (layerExists(layer)) {
                                        //adds an origin to a layer if it already exists and the replace key is null or false
                                        Layer existingLayer = ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(NamespacedKey.fromString(layer.getTag()));
                                        existingLayer.addOrigin(layer.getOrigins());
                                        ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).replaceEntry(NamespacedKey.fromString(layer.getTag()), existingLayer);
                                    } else {
                                        ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).register(layer);
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
                            JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath().replace(datapack.getName(), "") + origin_layer.getPath()));
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

                                    ArrayList<Power> powerContainers = new ArrayList<>();

                                    if (powersList != null) {
                                        for (String string : powersList) {
                                            boolean finished = false;
                                            if (((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).rawRegistry.containsKey(NamespacedKey.fromString(string))) {
                                                powerContainers.add(((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(string)));
                                                finished = true;
                                            }
                                            for (Power power : getNestedPowers(((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(string)))) {
                                                if (power != null) {
                                                    powerContainers.add(power);
                                                    finished = true;
                                                }
                                            }
                                            if (!finished) {
                                                // Not found in database, probably an error, move to backup parse to ensure all powers are added
                                                String[] powerLocation = string.split(":");
                                                String powerFolder = powerLocation[0];
                                                String powerFileName = powerLocation[1];

                                                try {

                                                    JSONObject powerParser = NamespaceRemapper.createRemapped(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json"));
                                                    if (powerParser.containsKey("type") && "apoli:multiple".equals(powerParser.get("type"))) {
                                                        Power powerContainer = new Power(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser),Utils.readJSONFileAsString(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")), false, true);
                                                        powerContainers.add(powerContainer);
                                                        processNestedPowers(powerContainer, powerContainers, powerFolder, powerFileName);
                                                    } else {
                                                        powerContainers.add(new Power(new NamespacedKey(powerFolder, powerFileName), fileToFileContainer(powerParser), Utils.readJSONFileAsString(new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + powerFolder + File.separator + "powers" + File.separator + powerFileName + ".json")), false));
                                                    }
                                                } catch (Exception fileNotFoundException) {
                                                    if (showErrors)
                                                        System.err.println("[GenesisMC] Error parsing \"%powerFolder%:%powerFileName%\" for \"%originFolder%:%originFileName%\"".replace("%powerFolder", powerFolder).replace("%powerFileName", powerFileName).replace("%originFolder%", originFolder.get(0)).replace("%originFileName%", originFileName.get(0)));
                                                }
                                            }
                                        }
                                    }
                                    Origin origin = new Origin(new NamespacedKey(originFolder.get(0), originFileName.get(0)), fileToFileContainer(originLayerParser), fileToHashMap(originParser), powerContainers);
                                    ((Registrar<Origin>)GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).register(origin);

                                } catch (Exception fileNotFoundException) {
                                    fileNotFoundException.printStackTrace();
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
            TagRegistryParser.runParse();
        }).thenRun(() -> {
            // Register builtin powers
            Method registerMethod;
            try {
                registerMethod = CraftPower.class.getDeclaredMethod("registerBuiltinPowers");
                registerMethod.setAccessible(true);
                registerMethod.invoke(null);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> {
            ConditionExecutor.registerAll();
        });

        future.get();
    }

    public static void unloadData() {
        GenesisMC.getPlugin().registry.clearRegistries();
    }

    /**
     * @return The HashMap serialized into a byte array.
     **/
    public static String toSaveFormat(HashMap<Layer, Origin> origin, Player p) {
        StringBuilder data = new StringBuilder();
        for (Layer layer : origin.keySet()) {
            Origin layerOrigins = origin.get(layer);
            ArrayList<String> powers = new ArrayList<>();
            for (Power power : OriginPlayerAccessor.playerPowerMapping.get(p).get(layer)) {
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
    public static String toOriginSetSaveFormat(HashMap<Layer, Origin> origin) {
        StringBuilder data = new StringBuilder();
        for (Layer layer : origin.keySet()) {
            Origin layerOrigins = origin.get(layer);
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
    public static Origin toOrigin(String originData, Layer originLayer) {
        if (originData != null) {
            try {
                String[] layers = originData.split("\n");
                for (String layer : layers) {
                    String[] layerData = layer.split("\\|");
                    if (((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(NamespacedKey.fromString(layerData[0])).equals(originLayer)) {
                        return CraftApoli.getOrigin(layerData[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return CraftApoli.nullOrigin();
            }
        }
        return CraftApoli.nullOrigin();
    }

    /**
     * @return The byte array deserialized into a HashMap of the originLayer and the OriginContainer.
     **/
    public static HashMap<Layer, Origin> toOrigin(String originData) {
        HashMap<Layer, Origin> containedOrigins = new HashMap<>();
        if (originData == null) {
            ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).forEach((key, layer) -> {
                containedOrigins.put(layer, CraftApoli.nullOrigin());
            });
        } else {
            try {
                String[] layers = originData.split("\n");
                for (String layer : layers) {
                    String[] layerData = layer.split("\\|");
                    Layer layerContainer = ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(NamespacedKey.fromString(layerData[0]));
                    Origin originContainer = CraftApoli.getOrigin(layerData[1]);
                    containedOrigins.put(layerContainer, originContainer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((Registrar<Layer>)GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).forEach((key, layer) -> {
                    containedOrigins.put(layer, CraftApoli.nullOrigin());
                });
                return containedOrigins;
            }
        }
        return containedOrigins;
    }

    /**
     * @return True if an origin is part of the core origins.
     **/
    public static Boolean isCoreOrigin(Origin origin) {
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

}
