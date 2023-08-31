package me.dueris.genesismc.factory.conditions.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;

public class ItemCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, ItemStack item, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {

        }
        return Optional.of(false);
    }
}
