package me.dueris.genesismc.factory.conditions.block;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.CraftCondition;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BlockCondition implements Condition {
    @Override
    public String condition_type() {
        return "BLOCK_CONDITION";
    }
    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (origin == null) return Optional.empty();
        if (origin.getPowerFileFromType(powerfile) == null) return Optional.empty();
        if (origin.getPowerFileFromType(powerfile).getCondition().isEmpty()) return Optional.empty();
            if (condition.isEmpty()) return Optional.empty();
            if (condition.get("type") == null) return Optional.empty();
            boolean inverted = (boolean) condition.getOrDefault("inverted", false);
            String type = condition.get("type").toString().toLowerCase();

            switch (type) {
                case "origins:in_tag" -> {
//                    String tag_name = condition.get("tag").toString();
//                    List<String> tagValues = inTagValues.get(origin.getPowerFileFromType(powerfile));
//
//                    if (tagValues == null) {
//                        tagValues = new ArrayList<>();
//                        for (File datapack : CraftApoli.datapacksInDir()) {
//                            File tagFile = new File(datapack.getAbsolutePath() + File.separator + "data" + File.separator + tag_name.split(":")[0] + File.separator + "tags" + File.separator + "blocks" + File.separator + tag_name.split(":")[1] + ".json");
//
//                            if (tagFile.exists()) {
//                                Gson gson = new Gson();
//                                JsonParser parser = new JsonParser();
//
//                                JsonElement jsonElement = null;
//                                try {
//                                    jsonElement = parser.parse(new FileReader(tagFile));
//                                } catch (FileNotFoundException e) {
//                                    throw new RuntimeException(e);
//                                }
//
//                                if (jsonElement.isJsonObject()) {
//                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
//                                    JsonElement valuesElement = jsonObject.get("values");
//
//                                    if (valuesElement != null) {
//                                        if (valuesElement.isJsonArray()) {
//                                            for (JsonElement value : valuesElement.getAsJsonArray()) {
//                                                tagValues.add(value.getAsString());
//                                            }
//                                        } else if (valuesElement.isJsonObject()) {
//                                            for (Map.Entry<String, JsonElement> entry : valuesElement.getAsJsonObject().entrySet()) {
//                                                tagValues.add(entry.getValue().getAsString());
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        inTagValues.put(origin.getPowerFileFromType(powerfile), (ArrayList<String>) tagValues);
//                    }
//                    if (tagValues.isEmpty()) {
//                        //i hate this
//                    } else {
//                        for (String value : tagValues) {
//                            if (block.getType().equals(Material.matchMaterial(value))) {
//                                return Optional.of(true);
//                            }
//                        }
//                    }
                }
                case "origins:material" -> {
                    Material mat = Material.valueOf(condition.get("material").toString().split(":")[1].toUpperCase());
                    if(block.getType().equals(mat)) return Optional.of(true);
                }
            }
            return getResult(inverted, false);
    }
}
