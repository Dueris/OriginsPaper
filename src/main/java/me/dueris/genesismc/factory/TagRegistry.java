package me.dueris.genesismc.factory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.tag.BaseTag;
import me.dueris.genesismc.files.GenesisDataFiles;
import net.kyori.adventure.sound.Sound;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.BlastingRecipe;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public enum TagRegistry {
    BA;
    //KEY: file_tag, values[]
    HashMap<String, ArrayList<String>> files = new HashMap<>();

    @Override
    public String toString() {
        return super.toString();
    }

    public HashMap<String, ArrayList<String>> getFiles() {
        return files;
    }

    public static void runParse() {
        Boolean showErrors = Boolean.valueOf(GenesisDataFiles.getMainConfig().get("console-print-parse-errors").toString());
        File DatapackDir = new File(Bukkit.getServer().getPluginManager().getPlugin("GenesisMC").getDataFolder() + File.separator + ".." + File.separator + ".." + File.separator + Bukkit.getServer().getWorlds().get(0).getName() + File.separator + "datapacks");
        File[] datapacks = DatapackDir.listFiles();
        if (datapacks == null) return;

        for (File datapack : datapacks) {
            File dataDir = new File(datapack.getAbsolutePath() + File.separator + "data");
            if (!dataDir.isDirectory()) continue;
            for (File mainDIR : dataDir.listFiles()) { //line 38
                for (File insideDIR : mainDIR.listFiles()) {
                    if(!insideDIR.getName().equalsIgnoreCase("tags")) continue;
                    for (File tagFolder : insideDIR.listFiles()) {
                        if (tagFolder.getName().equalsIgnoreCase("blocks")) {
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            // Handle values starting with #minecraft recursively
                                                            String minecraftTag = valueStr.substring("#minecraft:".length());
                                                            processMinecraftTag(minecraftTag);
                                                        } else {
                                                            // Handle regular values
                                                            Bukkit.getLogger().info(valueStr);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (tagFolder.getName().equalsIgnoreCase("items")) {

                        }
                        if (tagFolder.getName().equalsIgnoreCase("fluid")) {

                        }
                        if (tagFolder.getName().equalsIgnoreCase("entity")) {

                        }
                        if (tagFolder.getName().equalsIgnoreCase("biome")) {

                        }
                        if (tagFolder.getName().equalsIgnoreCase("entity_type")) {

                        }
                        if (tagFolder.getName().equalsIgnoreCase("damage")) {

                        }
                    }
                }
            }
        }
    }

    private static void processMinecraftTag(String tag) {
        NamespacedKey saplingsKey = new NamespacedKey("minecraft", "saplings");
        Bukkit.getServer().getTag("blocks", saplingsKey, Material.class);
        Bukkit.getLogger().info("Expanding Minecraft tag: " + tag);
    }

}
