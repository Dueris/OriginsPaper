package me.dueris.genesismc.factory;

import com.google.gson.JsonParser;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
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

    public static List<Layer> getLayersFromRegistry() {
        return GenesisMC.getPlugin().registry.retrieve(Registries.LAYER).values().stream().toList();
    }

    public static List<Origin> getOriginsFromRegistry() {
        return ((Registrar<Origin>) GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).values().stream().toList();
    }

    public static List<Power> getPowersFromRegistry() {
        return ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).values().stream().toList();
    }

    public static Origin getOrigin(String originTag) {
        for (Origin o : ((Registrar<Origin>) GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN)).values())
            if (o.getTag().equals(originTag)) return o;
        return nullOrigin();
    }

    public static Layer getLayerFromTag(String layerTag) {
        for (Layer l : ((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).values())
            if (l.getTag().equals(layerTag)) return l;
        return ((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(new NamespacedKey("origins", "origin"));
    }

    public static Power getPowerFromTag(String powerTag) {
        for (Power p : ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).values())
            if (p.getTag().equals(powerTag)) return p;
        return null;
    }

    /**
     * @return A copy of The null origin.
     **/
    public static Origin nullOrigin() {
        return new Origin(
            GenesisMC.originIdentifier("empty"),
            new ArrayList<>(),
            new FactoryJsonObject(
                JsonParser.parseString("{\"icon\":{\"item\":\"minecraft:player_head\"},\"name\":\"Null\",\"description\":\"Still Null\",\"order\":0,\"impact\":0}").getAsJsonObject()
            )
        );
    }

    public static void processNestedPowers(Power powerContainer, ArrayList<Power> powerContainers, String powerFolder, String powerFileName, File sourceFile) {
        for (String key : powerContainer.keySet()) {
            FactoryElement subPowerValue = powerContainer.getElement(key);
            if (subPowerValue.isJsonObject()) {
                FactoryJsonObject jsonObject = subPowerValue.toJsonObject();
                FactoryBuilder accessor = new FactoryBuilder(subPowerValue.handle, sourceFile);

                Power newPower = new Power(new NamespacedKey(powerFolder, powerFileName + "_" + key.toLowerCase()), jsonObject, true, false, powerContainer, accessor);
                ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).register(newPower);
            }
        }
    }

    public static ArrayList<Power> getNestedPowers(Power power) {
        ArrayList<Power> nested = new ArrayList<>();
        if (power == null) return nested;
        String powerFolder = power.getTag().split(":")[0].toLowerCase();
        String powerFileName = power.getTag().split(":")[1].toLowerCase();

        for (String key : power.keySet()) {
            if (power.getElement(key).isJsonObject()) {
                if (((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(new NamespacedKey(powerFolder, powerFileName + "_" + key.toLowerCase())) != null) {
                    nested.add(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(powerFolder + ":" + powerFileName + "_" + key.toLowerCase())));
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
            List<String> powers = layerOrigins.getPowers();
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
                    if (((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(NamespacedKey.fromString(layerData[0])).equals(originLayer)) {
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
            ((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).forEach((key, layer) -> {
                containedOrigins.put(layer, CraftApoli.nullOrigin());
            });
        } else {
            try {
                String[] layers = originData.split("\n");
                for (String layer : layers) {
                    String[] layerData = layer.split("\\|");
                    Layer layerContainer = ((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).get(NamespacedKey.fromString(layerData[0]));
                    Origin originContainer = CraftApoli.getOrigin(layerData[1]);
                    containedOrigins.put(layerContainer, originContainer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((Registrar<Layer>) GenesisMC.getPlugin().registry.retrieve(Registries.LAYER)).forEach((key, layer) -> {
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
