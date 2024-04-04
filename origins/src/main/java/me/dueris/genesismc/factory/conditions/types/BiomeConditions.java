package me.dueris.genesismc.factory.conditions.types;

import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.world.BiomeMappings;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome.Precipitation;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.json.simple.JSONObject;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiPredicate;

public class BiomeConditions {

    public static HashMap<String, ArrayList<Biome>> biomeTagMappings = new HashMap<>();

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, biome) -> {
            NamespacedKey tag = NamespacedKey.fromString(condition.get("tag").toString());
            TagKey key = TagKey.create(net.minecraft.core.registries.Registries.BIOME, CraftNamespacedKey.toMinecraft(tag));
            return ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).wrapAsHolder(biome.getA()).is(key);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("precipitation"), (condition, biome) -> {
            return biome.getA().hasPrecipitation() ?
            /*
                biome.getA().getTemperature(biome.getB()) <= 0.15f ?
                    condition.get("precipitation").toString().equals("snow")
                    : condition.get("precipitation").toString().equals("rain")
                : condition.get("precipitation").toString().equals("none");
                    */
                biome.getA().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(getPrecipitation(condition)) :
                    condition.get("precipitation").toString().toLowerCase().equals("none");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("category"), (condition, biome) -> {
            Biome b = CraftBiome.minecraftToBukkit(biome.getA());
            for (String biS : BiomeMappings.getBiomeIDs(condition.get("category").toString())) {
                if (Biome.valueOf(biS.split(":")[1].toUpperCase()).equals(b)) {
                    return true;
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("temperature"), (condition, biome) -> {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            return Comparison.getFromString(comparison).compare(biome.getA().getBaseTemperature(), compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("high_humidity"), (condition, biome) -> {
            return Comparison.getFromString(">=").compare(biome.getA().climateSettings.downfall(), 0.85f);
        }));
    }

    private net.minecraft.world.level.biome.Biome.Precipitation getPrecipitation(JSONObject condition) {
        String lowerCase = condition.get("precipitation").toString().toLowerCase();
        switch(lowerCase) {
            case "none" -> {
                return Precipitation.NONE;
            }
            case "snow" -> {
                return Precipitation.SNOW;
            }
            case "rain" -> {
                return Precipitation.RAIN;
            }
            default -> {
                return null;
            }
        }
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, Pair<net.minecraft.world.level.biome.Biome, BlockPos>> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, Pair<net.minecraft.world.level.biome.Biome, BlockPos>> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, Pair<net.minecraft.world.level.biome.Biome, BlockPos> tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
