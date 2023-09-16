package me.dueris.genesismc.factory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.tag.BaseTag;
import it.unimi.dsi.fastutil.Hash;
import me.dueris.genesismc.files.GenesisDataFiles;
import net.kyori.adventure.sound.Sound;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
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
import java.util.Map;

public enum TagRegistry {
    BA;

    @Override
    public String toString() {
        return super.toString();
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
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "blocks", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "items", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                        if (tagFolder.getName().equalsIgnoreCase("fluid")) {
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "fluid", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                        if (tagFolder.getName().equalsIgnoreCase("entity")) {
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "entity", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                        if (tagFolder.getName().equalsIgnoreCase("biome")) {
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "biome", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                        if (tagFolder.getName().equalsIgnoreCase("entity_type")) {
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "entity_type", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                        if (tagFolder.getName().equalsIgnoreCase("damage")) {
                            for (File jsonTagFile : tagFolder.listFiles()) {
                                if (jsonTagFile.getName().endsWith(".json")) {
                                    try (FileReader reader = new FileReader(jsonTagFile)) {
                                        JsonParser jsonParser = new JsonParser();
                                        String fileTag = mainDIR.getName() + ":" + jsonTagFile.getName().replace(".json", "");
                                        System.out.println("Registered OriginTag with id of :" + fileTag);
                                        JsonElement jsE = jsonParser.parse(reader);
                                        if (jsE.isJsonObject()) {
                                            JsonObject jsonObject = jsE.getAsJsonObject();
                                            if (jsonObject.has("values")) {
                                                JsonElement valE = jsonObject.get("values");
                                                if (valE.isJsonArray()) {
                                                    for (JsonElement value : valE.getAsJsonArray()) {
                                                        String valueStr = value.getAsString();
                                                        if (valueStr.startsWith("#minecraft:")) {
                                                            processMinecraftTag(valueStr, "damage", fileTag);
                                                        } else {
                                                            if(registered.containsKey(fileTag)){
                                                                registered.get(fileTag).add(valueStr);
                                                            }else{
                                                                registered.put(fileTag, new ArrayList<>());
                                                                registered.get(fileTag).add(valueStr);
                                                            }
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
                    }
                }
            }
        }
    }

    private static void processMinecraftTag(String tag, String registry, String fileTag) {
        String[] tagS = tag.split("#")[1].split(":");
        NamespacedKey saplingsKey = new NamespacedKey(tagS[0], tagS[1]);
        if(registry.toString().equals("entity_type")){
            if(Bukkit.getServer().getTag("entity_type", saplingsKey, EntityType.class) == null) return;
            Bukkit.getServer().getTag(registry, saplingsKey, EntityType.class);
            for(EntityType s : Bukkit.getServer().getTag("entity_type", saplingsKey, EntityType.class).getValues()){
                if(registered.containsKey(fileTag)){
                    registered.get(fileTag).add(s.getKey().toString());
                }else{
                    registered.put(fileTag, new ArrayList<>());
                    registered.get(fileTag).add(s.getKey().toString());
                }
            }
        } else if (registry.equals("biome")) {
            if(Bukkit.getServer().getTag("biome", saplingsKey, Biome.class) == null) return;
            Bukkit.getServer().getTag(registry, saplingsKey, Biome.class);
            for(Biome s : Bukkit.getServer().getTag("biome", saplingsKey, Biome.class).getValues()) {
                if(registered.containsKey(fileTag)){
                    registered.get(fileTag).add(s.getKey().toString());
                }else{
                    registered.put(fileTag, new ArrayList<>());
                    registered.get(fileTag).add(s.getKey().toString());
                }
            }
        } else if (registry.equals("fluid")) {
            if(Bukkit.getServer().getTag("fluid", saplingsKey, Fluid.class) == null) return;
            Bukkit.getServer().getTag(registry, saplingsKey, Fluid.class);
            for(Fluid s : Bukkit.getServer().getTag("fluid", saplingsKey, Fluid.class).getValues()){
                if(registered.containsKey(fileTag)){
                    registered.get(fileTag).add(s.getKey().toString());
                }else{
                    registered.put(fileTag, new ArrayList<>());
                    registered.get(fileTag).add(s.getKey().toString());
                }
            }
        }else{
            Bukkit.getServer().getTag(registry, saplingsKey, Material.class);
            if(Bukkit.getServer().getTag("blocks", saplingsKey, Material.class) == null) return;
            for(Material s : Bukkit.getServer().getTag("blocks", saplingsKey, Material.class).getValues()){
                if(registered.containsKey(fileTag)){
                    registered.get(fileTag).add(s.getKey().toString());
                }else{
                    registered.put(fileTag, new ArrayList<>());
                    registered.get(fileTag).add(s.getKey().toString());
                }
            }
        }
    }

    public static HashMap<String, ArrayList<String>> registered = new HashMap();

    public static ArrayList<String> getRegisteredTagFromFileKey(String fileTag){
        if(registered.containsKey(fileTag)){
            return registered.get(fileTag);
        }
        return null;
    }

}
