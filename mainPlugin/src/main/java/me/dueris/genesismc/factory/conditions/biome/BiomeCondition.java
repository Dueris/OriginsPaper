package me.dueris.genesismc.factory.conditions.biome;

import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.core.BlockPos;
import org.bukkit.Fluid;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBiome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiomeCondition implements Condition {

    @Override
    public String condition_type() {
        return "BIOME_CONDITION";
    }

    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();
    public static HashMap<String, ArrayList<Biome>> biomeTagMappings = new HashMap<>();

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (block != null && block.getBiome() != null) {
            boolean inverted = (boolean) condition.getOrDefault("inverted", false);
            String type = condition.get("type").toString().toLowerCase();
            switch(type){
                case "origins:biome" -> {
                    Map<String, Object> keyMap = (Map<String, Object>) condition.get("condition");
                    if (keyMap.containsKey("type") && keyMap.get("type").equals("origins:temperature")) {
                        if (keyMap.containsKey("comparison") && keyMap.containsKey("compare_to")) {
                            if (RestrictArmor.compareValues(block.getTemperature(), keyMap.get("comparison").toString(), Double.parseDouble(keyMap.get("compare_to").toString()))) {
                                return getResult(inverted, true);
                            }
                        }
                    }
                }
                case "origins:in_tag" -> {
                    // Use block in_tag optimization
                    if(TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null){
                        if(!biomeTagMappings.containsKey(condition.get("tag"))){
                            for(String mat : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())){
                                biomeTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                                biomeTagMappings.get(condition.get("tag")).add(Biome.valueOf(mat.split(":")[1].toUpperCase()));
                            }
                        }else{
                            // mappings exist, now we can start stuff
                            return Optional.of(biomeTagMappings.get(condition.get("tag")).contains(block.getBiome()));
                        }
                    }
                }
                case "origins:precipitation" -> {
                    Biome biome = block.getBiome();
                    if (biome != null) {
                        net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(biome);
                        if (b.coldEnoughToSnow(BlockPos.containing(block.getX(), block.getY(), block.getZ()))){
                            return getResult(inverted, condition.get("precipitation").toString().equals("snow"));
                        } else if (b.hasPrecipitation()) {
                            return getResult(inverted, condition.get("precipitation").toString().equals("rain"));
                        } else {
                            return getResult(inverted, condition.get("precipitation").toString().equals("none"));
                        }
                    } else {
                        return getResult(inverted, condition.get("precipitation").toString().equals("rain"));
                    }
                }
                case "origins:category" -> {
                    Biome biome = block.getBiome();
                    for(String biom : BiomeMappings.getBiomeIDs(condition.get("category").toString())){
                         if(Biome.valueOf(biom.split(":")[1].toUpperCase()).equals(biome)){
                             return getResult(inverted, true);
                         }
                    }
                }
                case "origins:temperature" -> {
                    net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(block.getBiome());
                    String comparison = condition.get("comparison").toString();
                    float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                        return getResult(inverted, RestrictArmor.compareValues(b.getBaseTemperature(), comparison, compare_to));
                }
                case "origins:humidity" -> {
                    net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(block.getBiome());
                    String comparison = condition.get("comparison").toString();
                    float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                        return getResult(inverted, RestrictArmor.compareValues(b.climateSettings.downfall(), comparison, compare_to));
                }
                default -> {
                    return getResult(inverted, false);
                }
            }
            return getResult(inverted, false);
        }else{
            return Optional.empty();
        }
    }
}
