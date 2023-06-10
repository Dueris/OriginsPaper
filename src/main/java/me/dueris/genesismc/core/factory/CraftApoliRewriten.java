package me.dueris.genesismc.core.factory;
import me.dueris.genesismc.core.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.nio.file.*;
import java.util.zip.ZipInputStream;

public class CraftApoliRewriten {

    private static final OriginContainer null_Origin = new OriginContainer("genesis:origin-null", new HashMap<String, Object>(Map.of( "hidden", true, "origins", "genesis:origin-null")), new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:null", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new PowerContainer("genesis:null", new PowerFileContainer(new ArrayList<>(), new ArrayList<>()), "genesis:origin-null"))));
    private static final OriginContainer choosing_Origin = new OriginContainer("genesis:origin-choosing", new HashMap<String, Object>(Map.of( "hidden", true, "origins", "genesis:origin-choosing")), new HashMap<String, Object>(Map.of("impact", "0", "icon", "minecraft:player_head", "powers", "genesis:choosing", "order", "0", "unchooseable", true)), new ArrayList<>(List.of(new PowerContainer("genesis:choosing", new PowerFileContainer(new ArrayList<>(), new ArrayList<>()), "genesis:origin-null"))));

    public static OriginContainer nullOrigin() {
        return null_Origin;
    }

    public static OriginContainer choosingOrigin(){return choosing_Origin;}

    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<OriginContainer> originContainers = new ArrayList<>();

    /**
     * @return A copy of the CustomOrigin object array for all the origins that are loaded.
     **/
    public static ArrayList<OriginContainer> getOrigins() {
        return (ArrayList<OriginContainer>) originContainers.clone();
    }

    /**
     * Removes any unzipped datapacks that were created by GenesisMC in the custom_origins folder.
     **/
    public static void removeUnzippedDatapacks() {
        File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + ".." + File.separator + ".." + File.separator + Bukkit.getServer().getWorlds().get(0).getName() + File.separator + "datapacks");
        File[] originDatapacks = originDatapackDir.listFiles();
        if (originDatapacks == null) return;

        for (File file : originDatapacks) {
            try {
                if (file.getName().startsWith(".")) FileUtils.deleteDirectory(new File(file.toURI())); //Linux
                if (SystemUtils.IS_OS_WINDOWS) {
                    if (Boolean.parseBoolean(Files.getAttribute(Path.of(file.getAbsolutePath()), "dos:hidden", LinkOption.NOFOLLOW_LINKS).toString()))
                        FileUtils.deleteDirectory(new File(file.toURI())); //Windows
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Error trying to remove old custom origin \"" + file.getName() + "\" - This file was automatically unzipped by Genesis.");
            }
        }
    }

    /**
     * Unzips any folder in the custom_origins folder.
     **/
    public static void unzipDatapacks() {
        File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + ".." + File.separator + ".." + File.separator + Bukkit.getServer().getWorlds().get(0).getName() + File.separator + "datapacks");
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
                } else
                    destination = Path.of(FilenameUtils.removeExtension(unzippedDestinationFile.getParent() + "/." + unzippedDestinationFile.getName()) + "_UnzippedByGenesis");

                if (!Files.exists(destination)) Files.createDirectory(destination);
                else continue;

                FileInputStream fileInputStream = new FileInputStream(file);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {

                    Path path = destination.resolve(zipEntry.getName());
                    if (!path.startsWith(destination))
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Something went wrong ¯\\_(ツ)_/¯");

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

    private static void translateOrigins() {
        for (OriginContainer origin : getCoreOrigins()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                power.setName(Lang.getLocalizedString(power.getName()));
                power.setDescription(Lang.getLocalizedString(power.getDesription()));
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
                        if (path.equals(Path.of("data"+File.separator+"origins"+File.separator+"origin_layers"+File.separator+"origin.json"))) {
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
                                            powerContainers.add(new PowerContainer(powerFolder + ":" + powerFileName, fileToPowerFileContainer(powerParser), originFolder.get(0) + ":" + originFileName.get(0)));
                                        } catch (NullPointerException nullPointerException) {
                                            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \"" + powerFolder + ":" + powerFileName + "\" for \"" + originFolder.get(0) + ":" + originFileName.get(0) + "\"").color(TextColor.color(255, 0, 0)));
                                        }
                                    }
                                }

                                originContainers.add(new OriginContainer(originFolder.get(0) + ":" + originFileName.get(0), fileToHashMap(originLayerParser), fileToHashMap(originParser), powerContainers));

                            }
                        originFolder.remove(0);
                        originFileName.remove(0);
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

            ArrayList<String> originFolder = new ArrayList<>();
            ArrayList<String> originFileName = new ArrayList<>();

            try {
                JSONObject originLayerParser = (JSONObject) new JSONParser().parse(new FileReader(datapack.getAbsolutePath() + File.separator + "data" + File.separator + "origins" + File.separator + "origin_layers" + File.separator + "origin.json"));
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
                                    powerContainers.add(new PowerContainer(powerFolder + ":" + powerFileName, fileToPowerFileContainer(powerParser), originFolder.get(0) + ":" + originFileName.get(0)));
                                } catch (FileNotFoundException fileNotFoundException) {
                                    Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Error parsing \"" + powerFolder + ":" + powerFileName + "\" for \"" + originFolder.get(0) + ":" + originFileName.get(0) + "\"").color(TextColor.color(255, 0, 0)));
                                }
                            }
                        }

                        originContainers.add(new OriginContainer(originFolder.get(0) + ":" + originFileName.get(0), fileToHashMap(originLayerParser), fileToHashMap(originParser), powerContainers));

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
                || origin.getTag().equals("origins:ally")
                || origin.getTag().equals("origins:bee")
                || origin.getTag().equals("origins:creep")
                || origin.getTag().equals("origins:piglin")
                || origin.getTag().equals("origins:rabbit")
                || origin.getTag().equals("origins:sculkling")
                || origin.getTag().equals("origins:slimeling")
                || origin.getTag().equals("origins:starborne");
    }

    public static ArrayList<OriginContainer> getCoreOrigins() {
        ArrayList<OriginContainer> coreOrigins = new ArrayList<>();
        for (OriginContainer origin : getOrigins()) {
            if (isCoreOrigin(origin)) coreOrigins.add(origin);
        }
        return coreOrigins;
    }

}
