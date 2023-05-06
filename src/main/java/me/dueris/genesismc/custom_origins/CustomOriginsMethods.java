package me.dueris.genesismc.custom_origins;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CustomOriginsMethods {

    public static HashMap<String, String> customOrigins = new HashMap<>();

    static byte[] trim(byte[] bytes) {
        int i = bytes.length -1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    public static void unzipCustomOriginDatapacks() {
        File originDatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder(), "custom_origins");
        File[] originDatapacks = originDatapackDir.listFiles();
        if (originDatapacks == null) return;

        for (File file : originDatapacks) {
            if (!FilenameUtils.getExtension(file.getName()).equals("zip")) continue;

            try {
                File unzippedDestinationFile = new File(file.getAbsolutePath());
                Path destination = Path.of(FilenameUtils.removeExtension(unzippedDestinationFile.getPath()) + "_Unzipped");
                if (!Files.exists(destination)) Files.createDirectory(destination);
                else continue;

                FileInputStream fileInputStream = new FileInputStream(file);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {

                    Path path = destination.resolve(zipEntry.getName());
                    if (!path.startsWith(destination)) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] Something went wrong ¯\\_(ツ)_/¯");

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
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] Duplicate origin detected!");
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] \""+originFolder+":"+originFileName+"\" in \""+originDatapack.getName()+"\" (This one won't load).");
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[GenesisMC] \""+originFolder+":"+originFileName+"\" in \""+customOrigins.get(originFolder+":"+originFileName)+"\" (This one will still load).");
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
        if (value == null) return "No Name";
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

    public static Object getOriginPowerDetial(String originTag, String powerTag, String valueToParse) {
        String[] values = powerTag.split(":");

        if (Objects.equals(values[0], "origins")) {
            return null;
            //temporary way of dealing with build in origin powers
        }

        String dirName = customOrigins.get(originTag);
        File originDetails = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() +"/custom_origins/"+dirName+"/data/"+values[0]+"/powers/"+values[1]+".json");
        try {
            JSONObject parser = (JSONObject) new JSONParser().parse(new FileReader(originDetails.getAbsolutePath()));
            return parser.get(valueToParse);
        } catch (Exception e) {
            //e.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] Using power defaults for "+powerTag+" - \""+e.getMessage()+"\"");

            return null;
        }
    }

    public static String getCustomOriginPowerName(String originTag, String powerTag) {
        Object value = getOriginPowerDetial(originTag, powerTag, "name");
        if (value == null) return "No Name";
        return (String) value;
    }

    public static String getCustomOriginPowerDescription(String originTag, String powerTag) {
        Object value = getOriginPowerDetial(originTag, powerTag, "description");
        if (value == null) return "No Description";
        return (String) value;
    }

    public static boolean getCustomOriginPowerHidden(String originTag, String powerTag) {
        Object value = getOriginPowerDetial(originTag, powerTag, "hidden");
        if (value == null) return false;
        return (Boolean) value;
    }
}
