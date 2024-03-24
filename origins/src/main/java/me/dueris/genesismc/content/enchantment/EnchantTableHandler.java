package me.dueris.genesismc.content.enchantment;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import static org.bukkit.Material.ENDER_PEARL;

public class EnchantTableHandler implements Listener {
    public static EnumSet<Material> wearable;

    static {
        wearable = EnumSet.of(
                Material.PUMPKIN,
                Material.CARVED_PUMPKIN,
                Material.ELYTRA,
                Material.TURTLE_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_BOOTS,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_HELMET,
                Material.CHAINMAIL_BOOTS,
                Material.CHAINMAIL_CHESTPLATE,
                Material.CHAINMAIL_HELMET,
                Material.CHAINMAIL_LEGGINGS,
                Material.IRON_HELMET,
                Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS,
                Material.IRON_BOOTS,
                Material.GOLDEN_HELMET,
                Material.GOLDEN_CHESTPLATE,
                Material.GOLDEN_LEGGINGS,
                Material.GOLDEN_BOOTS,
                Material.DIAMOND_HELMET,
                Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS,
                Material.DIAMOND_BOOTS,
                Material.NETHERITE_HELMET,
                Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS,
                Material.NETHERITE_BOOTS
        );
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        boolean conflicts = false;
        Random r = new Random();
        // if(37300 > r.nextInt(37400)){
        if (true) {
            if (wearable.contains(e.getItem().getType()) || e.getItem().getType() == Material.BOOK) {
                for (Enchantment enchant : e.getEnchantsToAdd().keySet()) {
                    if (AnvilHandler.conflictenchantments.contains(enchant)) {
                        conflicts = true;
                        break;
                    }
                }
                if (!conflicts) {
                    int boundInt = r.nextInt(100);
                    if (boundInt <= 40) {
                        // lvl1
                        AnvilHandler.setWaterProtCustomEnchantLevel(1, e.getItem());
                    } else if (boundInt <= 65 && boundInt >= 41) {
                        // lvl2
                        AnvilHandler.setWaterProtCustomEnchantLevel(2, e.getItem());
                    } else if (boundInt <= 85 && boundInt >= 66) {
                        // lvl3
                        AnvilHandler.setWaterProtCustomEnchantLevel(3, e.getItem());
                    } else if (boundInt <= 100 && boundInt >= 86) {
                        // lvl4
                        AnvilHandler.setWaterProtCustomEnchantLevel(4, e.getItem());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGrind(PrepareResultEvent e) {
        if (e.getResult() != null) {
            if (e.getInventory().getType().equals(InventoryType.GRINDSTONE)) {
                ItemStack item = e.getResult();
                if (item == null) return;
                if (item.getEnchantments().containsKey(CraftEnchantment.minecraftToBukkit(AnvilHandler.minecraftEnchantment))) {
                    for (String loreString : item.getLore()) {
                        if (loreString.startsWith("Water Protection")) {
                            item.getLore().remove(loreString);
                        }
                    }
                    item.removeEnchantment(CraftEnchantment.minecraftToBukkit(AnvilHandler.minecraftEnchantment));
                }
            }
        }
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        if (e.getInventory().contains(infinpearl)) {
            e.setResult(null);
        }
    }
}
