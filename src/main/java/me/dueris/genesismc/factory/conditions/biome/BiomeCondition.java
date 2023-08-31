package me.dueris.genesismc.factory.conditions.biome;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Optional;

public class BiomeCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Entity p, Block block, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();
        Biome biome = block.getBiome();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return Optional.of(false);
    }
}
