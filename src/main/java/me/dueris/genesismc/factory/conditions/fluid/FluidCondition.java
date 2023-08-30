package me.dueris.genesismc.factory.conditions.fluid;

import org.bukkit.Fluid;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class FluidCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Fluid fluid, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return Optional.of(false);
    }
}
