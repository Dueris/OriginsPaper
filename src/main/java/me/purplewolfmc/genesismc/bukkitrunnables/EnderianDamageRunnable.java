package me.purplewolfmc.genesismc.bukkitrunnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static me.purplewolfmc.genesismc.GenesisMC.waterProtectionEnchant;

public class EnderianDamageRunnable extends BukkitRunnable {
    private final HashMap<UUID, Long> cooldown;
    public EnderianDamageRunnable() {
        this.cooldown = new HashMap<>();
    }
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.getScoreboardTags().contains("enderian")) {
                Block b = p.getWorld().getHighestBlockAt(p.getLocation());
                if(!(p.isInsideVehicle())){
                    if(p.isInWaterOrRainOrBubbleColumn()) {
                        if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                            float curhealth = (float) p.getHealth();
                                float helemt_modifier = 0;
                                float chestplate_modifier = 0;
                                float leggins_modifier = 0;
                                float boots_modifier = 0;
                                float prot1 = (float) 0.2;
                                float prot2 = (float) 0.4;
                                float prot3 = (float) 0.6;
                                float prot4 = (float) 0.9;
                                if (p.getInventory().getHelmet() != null) {
                                    if (p.getEquipment().getHelmet().getEnchantments().containsKey(waterProtectionEnchant)) {
                                        if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            helemt_modifier = prot1;
                                        } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            helemt_modifier = prot2;
                                        } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            helemt_modifier = prot3;
                                        } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            helemt_modifier = prot4;
                                        }
                                    } else {
                                        helemt_modifier = 0;
                                    }
                                }
                                if (p.getInventory().getChestplate() != null) {
                                    if (p.getEquipment().getChestplate().getEnchantments().containsKey(waterProtectionEnchant)) {
                                        if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            chestplate_modifier = prot1;
                                        } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            chestplate_modifier = prot2;
                                        } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            chestplate_modifier = prot3;
                                        } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            chestplate_modifier = prot4;
                                        }
                                    } else {
                                        chestplate_modifier = 0;
                                    }
                                }
                                if (p.getInventory().getLeggings() != null) {
                                    if (p.getEquipment().getLeggings().getEnchantments().containsKey(waterProtectionEnchant)) {
                                        if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            leggins_modifier = prot1;
                                        } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            leggins_modifier = prot2;
                                        } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            leggins_modifier = prot3;
                                        } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            leggins_modifier = prot4;
                                        }
                                    } else {
                                        leggins_modifier = 0;
                                    }
                                }
                                if (p.getInventory().getBoots() != null) {
                                    if (p.getEquipment().getBoots().getEnchantments().containsKey(waterProtectionEnchant)) {
                                        if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            boots_modifier = prot1;
                                        } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            boots_modifier = prot2;
                                        } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            boots_modifier = prot3;
                                        } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            boots_modifier = prot4;
                                        }
                                    } else {
                                        boots_modifier = 0;
                                    }
                                }
                            float basedamage = 4 - helemt_modifier - chestplate_modifier - leggins_modifier - boots_modifier;


                            if (p.getHealth() >= basedamage && p.getHealth() != 0 && p.getHealth() - basedamage != 0) {
                                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10.0F, 5.0F);
                                p.damage(0.0000001);
                                p.setHealth(curhealth - basedamage);
                            } else if (p.getHealth() <= basedamage && p.getHealth() != 0) {
                                p.setHealth(curhealth - curhealth);

                            }


                        }
                    }
                }
            }
        }
    }
}
