package me.dueris.genesismc.factory.conditions.biEntity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiEntityCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Entity actor, Entity target, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return getResult(inverted, false);
    }
}
