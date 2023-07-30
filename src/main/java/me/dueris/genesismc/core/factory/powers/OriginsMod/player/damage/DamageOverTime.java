package me.dueris.genesismc.core.factory.powers.OriginsMod.player.damage;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.burn;
import static me.dueris.genesismc.core.factory.powers.Powers.damage_over_time;

public class DamageOverTime extends BukkitRunnable {

    private Long interval;
    private int damage;
    private DamageSource damage_source;
    private String damage_type;
    private String protection_enchantment;
    private double protection_effectiveness;

    private int ticksE;

    public DamageOverTime() {
        this.interval = 20L;
        this.ticksE = 0;
        this.damage_type = "apoli:damage_over_time";
        this.protection_effectiveness = 1.0;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (damage_over_time.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    PowerContainer power = origin.getPowerFileFromType("origins:damage_over_time");
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.damageOverTime"));
                        return;
                    }
                    interval = power.getInterval();
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        if(p.getWorld().getDifficulty().equals(Difficulty.EASY)){
                            if(origin.getPowerFileFromType("origins:damage_over_time").get("damage_easy", origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1")) == null){
                                damage = Integer.parseInt(origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1"));
                            }else{
                                damage = Integer.parseInt(origin.getPowerFileFromType("origins:damage_over_time").get("damage_easy", origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1")));
                            }
                        }else{
                            damage = Integer.parseInt(origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1"));
                        }

                        protection_effectiveness = Double.parseDouble(origin.getPowerFileFromType("origins:damage_over_time").get("protection_effectiveness", "1"));

                        if(!ConditionExecutor.check(p, origin, "origins:damage_over_time", null, p)) return;

                        if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                            float helemt_modifier = 0;
                            float chestplate_modifier = 0;
                            float leggins_modifier = 0;
                            float boots_modifier = 0;
                            float prot1 = (float) protection_effectiveness;
                            float prot2 = (float) protection_effectiveness;
                            float prot3 = (float) protection_effectiveness;
                            float prot4 = (float) protection_effectiveness;
                            if (p.getInventory().getHelmet() != null) {
                                if (p.getInventory().getHelmet().getLore() != null) {
                                    if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection I") || p.getEquipment().getHelmet().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        helemt_modifier = prot1;
                                    } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection II") || p.getEquipment().getHelmet().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        helemt_modifier = prot2;
                                    } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection III") || p.getEquipment().getHelmet().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        helemt_modifier = prot3;
                                    } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection IV") || p.getEquipment().getHelmet().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        helemt_modifier = prot4;
                                    } else {
                                        helemt_modifier = 0;
                                    }
                                }
                            }
                            if (p.getInventory().getChestplate() != null) {

                                if (p.getInventory().getChestplate().getLore() != null) {
                                    if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection I") || p.getEquipment().getChestplate().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        chestplate_modifier = prot1;
                                    } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection II") || p.getEquipment().getChestplate().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        chestplate_modifier = prot2;
                                    } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection III") || p.getEquipment().getChestplate().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        chestplate_modifier = prot3;
                                    } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection IV") || p.getEquipment().getChestplate().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        chestplate_modifier = prot4;
                                    } else {
                                        chestplate_modifier = 0;
                                    }
                                }
                            }
                            if (p.getInventory().getLeggings() != null) {
                                if (p.getInventory().getLeggings().getLore() != null) {
                                    if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection I") || p.getEquipment().getLeggings().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        leggins_modifier = prot1;
                                    } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection II") || p.getEquipment().getLeggings().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        leggins_modifier = prot2;
                                    } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection III") || p.getEquipment().getLeggings().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        leggins_modifier = prot3;
                                    } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection IV") || p.getEquipment().getLeggings().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        leggins_modifier = prot4;
                                    } else {
                                        leggins_modifier = 0;
                                    }
                                }
                            }
                            if (p.getInventory().getBoots() != null) {
                                if (p.getInventory().getBoots().getLore() != null) {
                                    if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection I") || p.getEquipment().getBoots().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        boots_modifier = prot1;
                                    } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection II") || p.getEquipment().getBoots().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        boots_modifier = prot2;
                                    } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection III") || p.getEquipment().getBoots().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        boots_modifier = prot3;
                                    } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection IV") || p.getEquipment().getBoots().getItemMeta().hasEnchant(Enchantment.getByKey(new NamespacedKey("minecraft", protection_enchantment.split(":")[1])))) {
                                        boots_modifier = prot4;
                                    } else {
                                        boots_modifier = 0;
                                    }
                                }
                            }
                            float basedamage = damage - helemt_modifier - chestplate_modifier - leggins_modifier - boots_modifier;


                            if (p.getHealth() >= basedamage && p.getHealth() != 0 && p.getHealth() - basedamage != 0) {
                                p.damage(basedamage);

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

                        ticksE = 0;
                    }
                }
            }
        }
    }
}
