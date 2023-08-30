package me.dueris.genesismc.factory.conditions.biEntity;

import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class BiEntityCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Entity actor, Entity target, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return Optional.of(false);
    }
}
