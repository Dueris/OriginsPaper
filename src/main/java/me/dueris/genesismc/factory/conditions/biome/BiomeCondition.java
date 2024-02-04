package me.dueris.genesismc.factory.conditions.biome;

import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import net.minecraft.core.BlockPos;
import org.bukkit.Fluid;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiomeCondition implements Condition {

    public static HashMap<String, ArrayList<Biome>> biomeTagMappings = new HashMap<>();

    @Override
    public String condition_type() {
        return "BIOME_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (block != null && block.getBiome() != null) {
            boolean inverted = (boolean) condition.getOrDefault("inverted", false);
            String type = condition.get("type").toString().toLowerCase();
            switch (type) {
                case "apoli:in_tag" -> {
                    // Use block in_tag optimization
                    if (TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null) {
                        if (!biomeTagMappings.containsKey(condition.get("tag"))) {
                            biomeTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                            for (String mat : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                                try {
                                    biomeTagMappings.get(condition.get("tag")).add(Biome.valueOf(mat.split(":")[1].toUpperCase()));
                                } catch (IllegalArgumentException e) {
                                    // akjghdfkj
                                }
                            }
                        } else {
                            // mappings exist, now we can start stuff
                            return getResult(inverted, Optional.of(biomeTagMappings.get(condition.get("tag")).contains(block.getBiome())));
                        }
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
                }
                case "apoli:precipitation" -> {
                    Biome biome = block.getBiome();
                    if (biome != null) {
                        net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(biome);
                        if (b.coldEnoughToSnow(BlockPos.containing(block.getX(), block.getY(), block.getZ()))) {
                            return getResult(inverted, Optional.of(condition.get("precipitation").toString().equals("snow")));
                        } else if (b.hasPrecipitation()) {
                            return getResult(inverted, Optional.of(condition.get("precipitation").toString().equals("rain")));
                        } else {
                            return getResult(inverted, Optional.of(condition.get("precipitation").toString().equals("none")));
                        }
                    } else {
                        return getResult(inverted, Optional.of(condition.get("precipitation").toString().equals("rain")));
                    }
                }
                case "apoli:category" -> {
                    Biome biome = block.getBiome();
                    for (String biom : BiomeMappings.getBiomeIDs(condition.get("category").toString())) {
                        return getResult(inverted, Optional.of(Biome.valueOf(biom.split(":")[1].toUpperCase()).equals(biome)));
                    }
                }
                case "apoli:temperature" -> {
                    net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(block.getBiome());
                    String comparison = condition.get("comparison").toString();
                    float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(RestrictArmor.compareValues(b.getBaseTemperature(), comparison, compare_to)));
                }
                case "apoli:humidity" -> {
                    net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(block.getBiome());
                    String comparison = condition.get("comparison").toString();
                    float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(RestrictArmor.compareValues(b.climateSettings.downfall(), comparison, compare_to)));
                }
                default -> {
                    return getResult(inverted, Optional.empty());
                }
            }
            return getResult(inverted, Optional.empty());
        } else {
            return Optional.empty();
        }
    }
}
