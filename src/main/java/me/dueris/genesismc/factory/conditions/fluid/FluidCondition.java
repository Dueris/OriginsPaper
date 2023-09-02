package me.dueris.genesismc.factory.conditions.fluid;

import org.bukkit.Fluid;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class FluidCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Fluid fluid, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return getResult(inverted, false);
    }
}
