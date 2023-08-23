package me.dueris.genesismc.enchantments;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static me.dueris.genesismc.GenesisMC.waterProtectionEnchant;

public class WaterProtAnvil implements Listener {

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        ItemStack first = e.getInventory().getFirstItem();
        ItemStack second = e.getInventory().getSecondItem();
        ItemStack result = e.getInventory().getResult();
        Player p = (Player) e.getView().getPlayer();
        Material retype;
        String level;
        ItemMeta meta;
        if (first != null) {
            if (first.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey()))) {
                if (second != null && !second.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey()))) {
                    if (first.getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                        retype = first.getType();
                        e.getResult().setType(retype);
                        level = "I";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(1);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 1);
                    } else if (first.getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                        retype = first.getType();
                        e.getResult().setType(retype);
                        level = "II";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(2);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 2);
                    } else if (first.getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                        retype = first.getType();
                        e.getResult().setType(retype);
                        level = "III";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(3);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 3);
                        p.sendMessage("1.3");
                    } else if (first.getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                        retype = first.getType();
                        e.getResult().setType(retype);
                        level = "IV";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(4);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 4);
                        p.sendMessage("1.4");
                    }
                }
            } else {

            }
        }

        if (first != null && second != null) {
            if (second.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey()))) {
                if (!first.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && second != null) {
                    if (second.getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                        level = "I";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(1);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 1);
                    } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                        level = "II";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(2);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 2);
                    } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                        level = "III";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(3);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 3);
                        p.sendMessage("2.3");
                    } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                        level = "IV";
                        meta = e.getResult().getItemMeta();
                        meta.setCustomModelData(4);
                        e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                        e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 4);
                        p.sendMessage("2.4");
                    }
                }
            } else {

            }
        }

        if (first != null && second != null && second.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && first.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && first.getEnchantmentLevel(GenesisMC.waterProtectionEnchant) == second.getEnchantmentLevel(GenesisMC.waterProtectionEnchant)) {
            if (second.getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                if (e.getResult() != null) {
                    level = "II";
                    meta = e.getResult().getItemMeta();
                    meta.setCustomModelData(2);
                    e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                    e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 2);
                }
            } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                if (e.getResult() != null) {
                    level = "III";
                    meta = e.getResult().getItemMeta();
                    meta.setCustomModelData(3);
                    e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                    e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 3);
                }
            } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                if (e.getResult() != null) {
                    level = "IV";
                    meta = e.getResult().getItemMeta();
                    meta.setCustomModelData(4);
                    e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                    e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 4);
                }

                p.sendMessage("3.3");
            } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection IV") && e.getResult() != null) {
                level = "IV";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(4);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 4);
                p.sendMessage("3.4");
            }
        }

        if (first != null && second != null && second.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && first.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && first.getItemMeta().getEnchantLevel(waterProtectionEnchant) > second.getItemMeta().getEnchantLevel(waterProtectionEnchant)) {
            if (first.getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                retype = first.getType();
                e.getResult().setType(retype);
                level = "I";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(1);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 1);
            } else if (first.getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                retype = first.getType();
                e.getResult().setType(retype);
                level = "II";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(2);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 2);
            } else if (first.getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                retype = first.getType();
                e.getResult().setType(retype);
                level = "III";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(3);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 3);
            } else if (first.getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                retype = first.getType();
                e.getResult().setType(retype);
                level = "IV";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(4);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 4);
            }
        }

        if (first != null && second != null && second.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && first.getEnchantments().containsKey(Enchantment.getByKey(GenesisMC.waterProtectionEnchant.getKey())) && first.getEnchantmentLevel(GenesisMC.waterProtectionEnchant) < second.getEnchantmentLevel(GenesisMC.waterProtectionEnchant) && second != null) {
            if (second.getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                level = "I";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(1);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection I"));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 1);
            } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                level = "II";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(2);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection II"));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 2);
            } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                level = "III";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(3);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection III"));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 3);
            } else if (second.getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                level = "IV";
                meta = e.getResult().getItemMeta();
                meta.setCustomModelData(4);
                e.getResult().setLore(List.of(ChatColor.GRAY + "Water Protection VI"));
                e.getResult().addUnsafeEnchantment(GenesisMC.waterProtectionEnchant, 4);
            }
        }

    }
}
