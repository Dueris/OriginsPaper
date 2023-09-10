package me.dueris.genesismc.factory.conditions.item;

import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class ItemCondition implements Condition {
    @Override
    public String condition_type() {
        return "ITEM_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        if (type.equalsIgnoreCase("origins:ingredient")) {
            if (condition.containsKey("ingredient")) {
                Map<String, Object> ingredientMap = (Map<String, Object>) condition.get("ingredient");
                if (ingredientMap.containsKey("item")) {
                    String itemValue = ingredientMap.get("item").toString();
                    if (itemStack.getType().equals(Material.valueOf(itemValue.split(":")[1].toUpperCase()))) {
                        return getResult(inverted, true);
                    } else {
                        return getResult(inverted, false);
                    }
                }
            }
        }
        if (type.equalsIgnoreCase("origins:meat")) {
            if (itemStack.getType().isEdible()) {
                if (inverted) {
                    if (getNonMeatMaterials().contains(itemStack)) {
                        return getResult(false, true);
                    } else {
                        return getResult(false, false);
                    }
                } else {
                    if (getMeatMaterials().contains(itemStack)) {
                        return getResult(false, true);
                    } else {
                        return getResult(false, false);
                    }
                }
            } else {
                return getResult(false, false);
            }
        }
        return getResult(inverted, false);
    }

    public static List<Material> getNonMeatMaterials() {
        List<Material> nonMeatMaterials = new ArrayList<>();

        nonMeatMaterials.add(Material.APPLE);
        nonMeatMaterials.add(Material.CARROT);
        nonMeatMaterials.add(Material.POTATO);
        nonMeatMaterials.add(Material.BEETROOT);
        nonMeatMaterials.add(Material.WHEAT);
        nonMeatMaterials.add(Material.MELON_SLICE);
        nonMeatMaterials.add(Material.PUMPKIN);
        nonMeatMaterials.add(Material.BAMBOO);

        nonMeatMaterials.add(Material.KELP);
        nonMeatMaterials.add(Material.SEAGRASS);

        nonMeatMaterials.add(Material.MILK_BUCKET);
        nonMeatMaterials.add(Material.EGG);

        nonMeatMaterials.add(Material.SUGAR);
        nonMeatMaterials.add(Material.HONEY_BOTTLE);

        nonMeatMaterials.add(Material.WATER_BUCKET);
        nonMeatMaterials.add(Material.LAVA_BUCKET);

        nonMeatMaterials.add(Material.BREAD);
        nonMeatMaterials.add(Material.CAKE);
        nonMeatMaterials.add(Material.COOKIE);

        return nonMeatMaterials;
    }

    public static List<Material> getMeatMaterials() {
        List<Material> meatMaterials = new ArrayList<>();

        meatMaterials.add(Material.BEEF);
        meatMaterials.add(Material.PORKCHOP);
        meatMaterials.add(Material.MUTTON);
        meatMaterials.add(Material.CHICKEN);
        meatMaterials.add(Material.RABBIT);
        meatMaterials.add(Material.TROPICAL_FISH);
        meatMaterials.add(Material.COD);
        meatMaterials.add(Material.SALMON);
        meatMaterials.add(Material.PUFFERFISH);

        meatMaterials.add(Material.COOKED_BEEF);
        meatMaterials.add(Material.COOKED_PORKCHOP);
        meatMaterials.add(Material.COOKED_MUTTON);
        meatMaterials.add(Material.COOKED_CHICKEN);
        meatMaterials.add(Material.COOKED_RABBIT);

        meatMaterials.add(Material.ROTTEN_FLESH);

        return meatMaterials;
    }

}
