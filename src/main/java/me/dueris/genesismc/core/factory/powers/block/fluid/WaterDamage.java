package me.dueris.genesismc.core.factory.powers.block.fluid;

import io.papermc.paper.event.entity.WaterBottleSplashEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static me.dueris.genesismc.core.factory.powers.Powers.water_breathing;
import static me.dueris.genesismc.core.factory.powers.Powers.water_vulnerability;
import static me.dueris.genesismc.core.factory.powers.block.fluid.WaterBreathe.outofAIR;

public class WaterDamage extends BukkitRunnable implements Listener {
    private final HashMap<UUID, Long> cooldown;

    public WaterDamage() {
        this.cooldown = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (water_vulnerability.contains(p)) {
                if (!(p.isInsideVehicle())) {
                    if (p.isInWaterOrRainOrBubbleColumn()) {
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
                                if (p.getInventory().getHelmet().getLore() != null) {
                                    if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                        helemt_modifier = prot1;
                                    } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                        helemt_modifier = prot2;
                                    } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                        helemt_modifier = prot3;
                                    } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                        helemt_modifier = prot4;
                                    } else {
                                        helemt_modifier = 0;
                                    }
                                }
                            }
                            if (p.getInventory().getChestplate() != null) {
                                if (p.getInventory().getChestplate().getLore() != null) {
                                    if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                        chestplate_modifier = prot1;
                                    } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                        chestplate_modifier = prot2;
                                    } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                        chestplate_modifier = prot3;
                                    } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                        chestplate_modifier = prot4;
                                    } else {
                                        chestplate_modifier = 0;
                                    }
                                }
                            }
                            if (p.getInventory().getLeggings() != null) {
                                if (p.getInventory().getLeggings().getLore() != null) {
                                    if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                        leggins_modifier = prot1;
                                    } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                        leggins_modifier = prot2;
                                    } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                        leggins_modifier = prot3;
                                    } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                        leggins_modifier = prot4;
                                    } else {
                                        leggins_modifier = 0;
                                    }
                                }
                            }
                            if (p.getInventory().getBoots() != null) {
                                if (p.getInventory().getBoots().getLore() != null) {
                                    if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                        boots_modifier = prot1;
                                    } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                        boots_modifier = prot2;
                                    } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                        boots_modifier = prot3;
                                    } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                        boots_modifier = prot4;
                                    } else {
                                        boots_modifier = 0;
                                    }
                                }
                            }
                            float basedamage = 4 - helemt_modifier - chestplate_modifier - leggins_modifier - boots_modifier;


                            if (p.getHealth() >= basedamage && p.getHealth() != 0 && p.getHealth() - basedamage != 0) {
                                p.damage(0.0000001);
                                p.setHealth(curhealth - basedamage);

                                Random random = new Random();

                                int r = random.nextInt(3);
                                if (r == 1) {
                                    if (p.getInventory().getHelmet() != null) {
                                        int heldur = p.getEquipment().getHelmet().getDurability();
                                        p.getEquipment().getHelmet().setDurability((short) (heldur + 3));
                                    }
                                    if (p.getInventory().getChestplate() != null) {
                                        int chestdur = p.getEquipment().getChestplate().getDurability();
                                        p.getEquipment().getChestplate().setDurability((short) (chestdur + 3));
                                    }
                                    if (p.getInventory().getLeggings() != null) {
                                        int legdur = p.getEquipment().getLeggings().getDurability();
                                        p.getEquipment().getLeggings().setDurability((short) (legdur + 3));
                                    }
                                    if (p.getInventory().getBoots() != null) {
                                        int bootdur = p.getEquipment().getBoots().getDurability();
                                        p.getEquipment().getBoots().setDurability((short) (bootdur + 3));
                                    }

                                }
                            } else if (p.getHealth() <= basedamage && p.getHealth() != 0) {
                                p.setHealth(0.0f);
                            }


                        }
                    }
                }
            }
            if (water_breathing.contains(p)) {
                if (outofAIR.contains(p)) {
                    if (p.getRemainingAir() > 20) {
                        outofAIR.remove(p);
                    } else {
                        p.damage(2);
                        p.playSound(p, Sound.ENTITY_PLAYER_HURT_DROWN, 10, 1);
                    }

                }
            }
        }
    }

    @EventHandler
    public void OnDeathWater(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (water_vulnerability.contains(e.getPlayer())) {
            Random random = new Random();
            int r = random.nextInt(2);
            if (p.isInWaterOrRainOrBubbleColumn()) {
                e.setDeathMessage(p.getName() + " took a bath for too long");
            }
            p.getLocation().getWorld().dropItem(p.getLocation(), new ItemStack(Material.ENDER_PEARL, r));
        }
    }

    @EventHandler
    public void SplashEnderian(WaterBottleSplashEvent e) {
        if (e.getAffectedEntities() instanceof Player p) {
            if (water_vulnerability.contains(p)) ;
            p.damage(5);
        }
    }


}
