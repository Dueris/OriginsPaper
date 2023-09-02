package me.dueris.genesismc.factory.conditions.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BlockCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Block block, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return getResult(inverted, false);
    }
}
