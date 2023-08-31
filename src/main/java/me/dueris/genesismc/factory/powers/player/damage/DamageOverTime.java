package me.dueris.genesismc.factory.powers.player.damage;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class DamageOverTime extends CraftPower {

    private String damage_type;
    private Long interval;
    private int damage;
    private DamageSource damage_source;
    private String protection_enchantment;
    private double protection_effectiveness;
    private int ticksE;

    public DamageOverTime() {
        this.interval = 20L;
        this.ticksE = 0;
        this.damage_type = "origins:damage_over_time";
        this.protection_effectiveness = 1.0;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    @Override
    public void run(Player p) {
        if (damage_over_time.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                PowerContainer power = origin.getPowerFileFromType("origins:damage_over_time");
                if (power == null) continue;
                if (power.getInterval() == null) {
                    Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.damageOverTime"));
                    return;
                }
                interval = power.getInterval();
                if (ticksE < interval) {
                    ticksE++;
                    return;
                } else {
                    if (p.getWorld().getDifficulty().equals(Difficulty.EASY)) {
                        if (origin.getPowerFileFromType("origins:damage_over_time").get("damage_easy", origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1")) == null) {
                            damage = Integer.parseInt(origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1"));
                        } else {
                            damage = Integer.parseInt(origin.getPowerFileFromType("origins:damage_over_time").get("damage_easy", origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1")));
                        }
                    } else {
                        damage = Integer.parseInt(origin.getPowerFileFromType("origins:damage_over_time").get("damage", "1"));
                    }

                    protection_effectiveness = Double.parseDouble(origin.getPowerFileFromType("origins:damage_over_time").get("protection_effectiveness", "1"));
                    ConditionExecutor executor = new ConditionExecutor();
                    if (executor.check("condition", "conditions", p, origin, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        if (origin.getPowerFileFromType(getPowerFile()) == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);

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

                    } else {
                        if (origin.getPowerFileFromType(getPowerFile()) == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                    ticksE = 0;
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:damage_over_time";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return damage_over_time;
    }
}
