package me.purplewolfmc.genesismc.core.bukkitrunnables;

import me.purplewolfmc.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.purplewolfmc.genesismc.core.GenesisMC.waterProtectionEnchant;

public class EnchantRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            //boots
            if(p.getEquipment().getBoots() != null) {
                if (p.getEquipment().getBoots().getLore() != null) {
                    if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                        p.getEquipment().getBoots().addUnsafeEnchantment(waterProtectionEnchant, 2);
                    }
                    if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                        p.getEquipment().getBoots().addUnsafeEnchantment(waterProtectionEnchant, 1);
                    }
                    if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                        p.getEquipment().getBoots().addUnsafeEnchantment(waterProtectionEnchant, 3);
                    }
                    if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                        p.getEquipment().getBoots().addUnsafeEnchantment(waterProtectionEnchant, 4);
                    }
                }
            }
            //helmet
            if(p.getEquipment().getBoots() != null) {
                if (p.getEquipment().getHelmet() != null) {
                    if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                        p.getEquipment().getHelmet().addUnsafeEnchantment(waterProtectionEnchant, 2);
                    }
                    if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                        p.getEquipment().getHelmet().addUnsafeEnchantment(waterProtectionEnchant, 1);
                    }
                    if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                        p.getEquipment().getHelmet().addUnsafeEnchantment(waterProtectionEnchant, 3);
                    }
                    if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                        p.getEquipment().getHelmet().addUnsafeEnchantment(waterProtectionEnchant, 4);
                    }
                }
            }
                if(p.getEquipment().getBoots() != null) {
                    if (p.getEquipment().getLeggings() != null) {
                        //leggins
                        if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                            p.getEquipment().getLeggings().addUnsafeEnchantment(waterProtectionEnchant, 2);
                        }
                        if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                            p.getEquipment().getLeggings().addUnsafeEnchantment(waterProtectionEnchant, 1);
                        }
                        if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                            p.getEquipment().getLeggings().addUnsafeEnchantment(waterProtectionEnchant, 3);
                        }
                        if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                            p.getEquipment().getLeggings().addUnsafeEnchantment(waterProtectionEnchant, 4);
                        }
                    }
                }
                    if(p.getEquipment().getBoots() != null) {
            if(p.getEquipment().getChestplate() != null) {
                //chestplate
                if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                    p.getEquipment().getChestplate().addUnsafeEnchantment(waterProtectionEnchant, 2);
                }
                if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                    p.getEquipment().getChestplate().addUnsafeEnchantment(waterProtectionEnchant, 1);
                }
                if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                    p.getEquipment().getChestplate().addUnsafeEnchantment(waterProtectionEnchant, 3);
                }
                if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                    p.getEquipment().getChestplate().addUnsafeEnchantment(waterProtectionEnchant, 4);
                }
            }
            }
        }
    }
}
