package me.dueris.genesismc.enchantments;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import me.dueris.genesismc.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static me.dueris.genesismc.GenesisMC.waterProtectionEnchant;
import static org.bukkit.Material.ENDER_PEARL;

public class EnchantProtEvent implements Listener {

    public static EnumSet<Material> wearable;

    static {
        wearable = EnumSet.of(Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.ELYTRA, Material.TURTLE_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        Random random = new Random();
        boolean t = false;
        int r10 = random.nextInt(50);
        int r4 = random.nextInt(45);
        int r5 = random.nextInt(43);
        for(Enchantment enchantment : e.getEnchantsToAdd().keySet()){
            if(WaterProtAnvil.conflictenchantments.contains(enchantment)){
                t = true;
            }
        }
        if(t) {
            e.setCancelled(true);
        } else {
            final boolean b = r10 == 1 || r10 == 2 || r10 == 3 || r10 == 4 || r10 == 5 || r10 == 6 || r10 == 7 || r10 == 8 || r10 == 9 || r10 == 10;
            if (wearable.contains(e.getItem().getType())) {
                if (b) {
                    if (r4 <= 45) {
                        e.getEnchantsToAdd().remove(e.getEnchantsToAdd());
                        if (r5 == 1 || r5 == 14 || r5 == 15 || r5 == 5 || r5 == 6 || r5 == 17 || r5 == 18 || r5 == 19 || r5 == 20 || r5 == 21 || r5 == 22 || r5 == 23 || r5 == 24 || r5 == 25 || r5 == 26 || r5 == 27 || r5 == 28 || r5 == 29 || r5 == 30) {
                            String level = "I";
                            ItemMeta meta = e.getItem().getItemMeta();
                            meta.setCustomModelData(1);
                            e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                            e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 1);
                            int basexp = e.getExpLevelCost();
                            e.setExpLevelCost(basexp + 2);

                        } else if (r5 == 2 || r5 == 8 || r5 == 9 || r5 == 11 || r5 == 13 || r5 == 7 || r5 == 31 || r5 == 32 || r5 == 33 || r5 == 34 || r5 == 35) {
                            String level = "II";
                            ItemMeta meta = e.getItem().getItemMeta();
                            meta.setCustomModelData(2);
                            e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                            e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 2);
                            int basexp = e.getExpLevelCost();
                            e.setExpLevelCost(basexp + 3);
                        } else if (r5 == 3 || r5 == 10 || r5 == 12 || r5 == 36 || r5 == 37 || r5 == 38 || r5 == 39 || r5 == 40) {
                            String level = "III";
                            ItemMeta meta = e.getItem().getItemMeta();
                            meta.setCustomModelData(3);
                            e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                            e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 3);
                            int basexp = e.getExpLevelCost();
                            e.setExpLevelCost(basexp + 4);
                        } else if (r5 == 4 || r5 == 16 || r5 == 41 || r5 == 42) {
                            String level = "IV";
                            ItemMeta meta = e.getItem().getItemMeta();
                            meta.setCustomModelData(4);
                            e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                            e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 4);
                            int basexp = e.getExpLevelCost();
                            e.setExpLevelCost(basexp + 6);
                        }
                    }
                    if (r4 == 1 || r4 == 14 || r4 == 15 || r4 == 5 || r4 == 6 || r4 == 17 || r4 == 18 || r4 == 19 || r4 == 20 || r4 == 21 || r4 == 22 || r4 == 23 || r4 == 24 || r4 == 25 || r4 == 26 || r4 == 27 || r4 == 28 || r4 == 29 || r4 == 30) {
                        String level = "I";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(1);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 1);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 2);

                    } else if (r4 == 2 || r4 == 8 || r4 == 9 || r4 == 11 || r4 == 13 || r4 == 7 || r4 == 31 || r4 == 32 || r4 == 33 || r4 == 34 || r4 == 35) {
                        String level = "II";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(2);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 2);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 3);
                    } else if (r4 == 3 || r4 == 10 || r4 == 12 || r4 == 36 || r4 == 37 || r4 == 38 || r4 == 39 || r4 == 40) {
                        String level = "III";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(3);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 3);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 4);
                    } else if (r4 == 4 || r4 == 16 || r4 == 41 || r4 == 42) {
                        String level = "IV";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(4);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 4);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 6);
                    }


                }
            } else if (e.getItem().getType().equals(Material.BOOK)) {
                if (b) {
                    if (r4 == 1 || r4 == 14 || r4 == 5 || r4 == 17 || r4 == 18 || r4 == 19 || r4 == 20 || r4 == 21 || r4 == 22 || r4 == 23 || r4 == 24 || r4 == 25 || r4 == 26 || r4 == 27 || r4 == 28 || r4 == 29 || r4 == 30) {
                        String level = "I";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(1);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 1);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 2);

                    } else if (r4 == 2 || r4 == 8 || r4 == 9 || r4 == 11 || r4 == 13 || r4 == 7 || r4 == 31 || r4 == 32 || r4 == 33 || r4 == 34 || r4 == 35) {
                        String level = "II";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(2);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 2);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 3);
                    } else if (r4 == 3 || r4 == 10 || r4 == 12 || r4 == 36 || r4 == 37 || r4 == 38 || r4 == 39 || r4 == 40) {
                        String level = "III";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(3);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 3);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 4);
                    } else if (r4 == 4 || r4 == 16 || r4 == 15 || r4 == 6) {
                        String level = "IV";
                        ItemMeta meta = e.getItem().getItemMeta();
                        meta.setCustomModelData(4);
                        e.getItem().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getItem().addUnsafeEnchantment(waterProtectionEnchant, 4);
                        int basexp = e.getExpLevelCost();
                        e.setExpLevelCost(basexp + 6);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onGrind(PrepareResultEvent e) {
        if (e.getResult() != null) {
            if (e.getInventory().getType().equals(InventoryType.GRINDSTONE)) ;
            if (e.getResult().getLore() != null) {
                if (e.getResult().getLore().contains(ChatColor.GRAY + waterProtectionEnchant.getName() + " I") || e.getResult().getLore().contains(ChatColor.GRAY + waterProtectionEnchant.getName() + " II") || e.getResult().getLore().contains(ChatColor.GRAY + waterProtectionEnchant.getName() + " III") || e.getResult().getLore().contains(ChatColor.GRAY + waterProtectionEnchant.getName() + " IV") || e.getResult().getLore().contains(ChatColor.GRAY + waterProtectionEnchant.getName())) {
                    e.getResult().setLore(null);
                    e.getResult().getItemMeta().setLore(null);
                    e.getResult().removeEnchantment(waterProtectionEnchant);
                } else {
                    //do nothing
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
