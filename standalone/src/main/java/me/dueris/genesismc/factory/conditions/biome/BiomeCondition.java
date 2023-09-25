package me.dueris.genesismc.factory.conditions.biome;

import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBiome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiomeCondition implements Condition {

    @Override
    public String condition_type() {
        return "BIOME_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        if (type.equalsIgnoreCase("origins:biome") && condition.containsKey("condition")) {
            Map<String, Object> keyMap = (Map<String, Object>) condition.get("condition");
            if (keyMap.containsKey("type") && keyMap.get("type").equals("origins:temperature")) {
                if (keyMap.containsKey("comparison") && keyMap.containsKey("compare_to")) {
                    if (RestrictArmor.compareValues(block.getTemperature(), keyMap.get("comparison").toString(), Double.parseDouble(keyMap.get("compare_to").toString()))) {
                        return getResult(inverted, true);
                    }
                }
            }
        }
        if (type.equals("origins:in_tag")){
            for(String bi : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())){
                if(block.getBiome().equals(Biome.valueOf(bi.toString().split(":")[1].toUpperCase()))){
                    return Optional.of(true);
                }
            }
        }
        if (type.equalsIgnoreCase("origins:precipitation")){
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
        if (type.equalsIgnoreCase("origins:category")){
            Biome biome = block.getBiome();
            for(String biom : BiomeMappings.getBiomeIDs(condition.get("category").toString())){
                 if(Biome.valueOf(biom.split(":")[1].toUpperCase()).equals(biome)){
                     return getResult(inverted, true);
                 }
            }
        }
        if (type.equalsIgnoreCase("origins:temperature")){
            net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(block.getBiome());
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                return getResult(inverted, RestrictArmor.compareValues(b.getBaseTemperature(), comparison, compare_to));
        }
        if (type.equalsIgnoreCase("origins:humidity")){
            net.minecraft.world.level.biome.Biome b = CraftBiome.bukkitToMinecraft(block.getBiome());
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                return getResult(inverted, RestrictArmor.compareValues(b.climateSettings.downfall(), comparison, compare_to));
        }
        return getResult(inverted, false);
    }
}
