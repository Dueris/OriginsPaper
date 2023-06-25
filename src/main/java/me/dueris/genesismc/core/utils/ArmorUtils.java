package me.dueris.genesismc.core.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorUtils {
    public static double getArmorValue(ItemStack armorItem) {
        Material material = armorItem.getType();
        switch (material) {
            case NETHERITE_HELMET:
                return 3.0;
            case NETHERITE_CHESTPLATE:
                return 8.0;
            case NETHERITE_LEGGINGS:
                return 6.0;
            case NETHERITE_BOOTS:
                return 3.0;
            case DIAMOND_HELMET:
                return 3.0;
            case DIAMOND_CHESTPLATE:
                return 8.0;
            case DIAMOND_LEGGINGS:
                return 6.0;
            case DIAMOND_BOOTS:
                return 3.0;
            case IRON_HELMET:
                return 2.0;
            case IRON_CHESTPLATE:
                return 6.0;
            case IRON_LEGGINGS:
                return 5.0;
            case IRON_BOOTS:
                return 2.0;
            case GOLDEN_HELMET:
                return 2.0;
            case GOLDEN_CHESTPLATE:
                return 5.0;
            case GOLDEN_LEGGINGS:
                return 3.0;
            case GOLDEN_BOOTS:
                return 1.0;
            case CHAINMAIL_HELMET:
                return 2.0;
            case CHAINMAIL_CHESTPLATE:
                return 5.0;
            case CHAINMAIL_LEGGINGS:
                return 4.0;
            case CHAINMAIL_BOOTS:
                return 1.0;
            case LEATHER_HELMET:
                return 1.0;
            case LEATHER_CHESTPLATE:
                return 3.0;
            case LEATHER_LEGGINGS:
                return 2.0;
            case LEATHER_BOOTS:
                return 1.0;
            // Add cases for other armor materials as needed
            default:
                return 0.0; // Default armor value
        }
    }

    public static double getHelmetArmorValue(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null) {
            return getArmorValue(helmet);
        }
        return 0.0; // No helmet equipped
    }

    public static double getChestplateArmorValue(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null) {
            return getArmorValue(chestplate);
        }
        return 0.0; // No chestplate equipped
    }

    public static double getLeggingsArmorValue(Player player) {
        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings != null) {
            return getArmorValue(leggings);
        }
        return 0.0; // No leggings equipped
    }

    public static double getBootsArmorValue(Player player) {
        ItemStack boots = player.getInventory().getBoots();
        if (boots != null) {
            return getArmorValue(boots);
        }
        return 0.0; // No boots equipped
    }
}
