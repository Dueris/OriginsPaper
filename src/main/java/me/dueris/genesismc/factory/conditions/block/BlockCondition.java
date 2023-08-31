package me.dueris.genesismc.factory.conditions.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class BlockCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Block block, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return Optional.of(false);
    }
}
