package me.dueris.genesismc.factory.conditions.item;

import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemCondition {
    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, ItemStack item, String powerfile) {
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {
            case "origins:ingredient" -> {
                if (condition.containsKey("ingredient")) {
                    Map<String, Object> ingredientMap = (Map<String, Object>) condition.get("ingredient");
                    if (ingredientMap.containsKey("item")) {
                        String itemValue = ingredientMap.get("item").toString();
                        if(item.getType().equals(Material.valueOf(itemValue.toString().split(":")[1].toUpperCase()))){
                            return Optional.of(true);
                        }
                    }
                }
            }
        }
        return Optional.of(false);
    }
}
