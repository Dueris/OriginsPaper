package me.dueris.genesismc.factory.powers.player.damage;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DamageOverTime extends CraftPower {

    private final String damage_type;
    private Long interval;
    private int damage;
    private DamageSource damage_source;
    private String protection_enchantment;
    private double protection_effectiveness;
    private final int ticksE;

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

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);
        if (getPowerArray().contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.burn"));
                        return;
                    }
                    interval = power.getInterval();

                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE < interval) {
                        ticksE++;

                        ticksEMap.put(p, ticksE);
                        return;
                    } else {
                        if (p.getWorld().getDifficulty().equals(Difficulty.EASY)) {
                            if (power.get("damage_easy", power.get("damage", "1")) == null) {
                                damage = Integer.parseInt(power.get("damage", "1"));
                            } else {
                                damage = Integer.parseInt(power.get("damage_easy", power.get("damage", "1")));
                            }
                        } else {
                            damage = Integer.parseInt(power.get("damage", "1"));
                        }

                        protection_effectiveness = Double.parseDouble(power.get("protection_effectiveness", "1"));
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                            setActive(power.getTag(), true);

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
                                    if(p.getInventory().getHelmet().containsEnchantment(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection))){
                                        int lvl = p.getInventory().getHelmet().getEnchantmentLevel(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection));
                                        if(lvl == 1){
                                            helemt_modifier = prot1;
                                        } else if(lvl == 2){
                                            helemt_modifier = prot2;
                                        } else if(lvl == 3){
                                            helemt_modifier = prot3;
                                        } else if(lvl == 4){
                                            helemt_modifier = prot4;
                                        } else {
                                            helemt_modifier = 0;
                                        }
                                    }
                                }
                                if (p.getInventory().getChestplate() != null) {
                                    if(p.getInventory().getChestplate().containsEnchantment(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection))){
                                        int lvl = p.getInventory().getHelmet().getEnchantmentLevel(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection));
                                        if(lvl == 1){
                                            chestplate_modifier = prot1;
                                        } else if(lvl == 2){
                                            chestplate_modifier = prot2;
                                        } else if(lvl == 3){
                                            chestplate_modifier = prot3;
                                        } else if(lvl == 4){
                                            chestplate_modifier = prot4;
                                        } else {
                                            chestplate_modifier = 0;
                                        }
                                    }
                                }
                                if (p.getInventory().getLeggings() != null) {
                                    if(p.getInventory().getLeggings().containsEnchantment(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection))){
                                        int lvl = p.getInventory().getHelmet().getEnchantmentLevel(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection));
                                        if(lvl == 1){
                                            leggins_modifier = prot1;
                                        } else if(lvl == 2){
                                            leggins_modifier = prot2;
                                        } else if(lvl == 3){
                                            leggins_modifier = prot3;
                                        } else if(lvl == 4){
                                            leggins_modifier = prot4;
                                        } else {
                                            leggins_modifier = 0;
                                        }
                                    }
                                }
                                if (p.getInventory().getBoots() != null) {
                                    if(p.getInventory().getBoots().containsEnchantment(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection))){
                                        int lvl = p.getInventory().getHelmet().getEnchantmentLevel(CraftEnchantment.minecraftToBukkit(me.dueris.genesismc.Bootstrap.waterProtection));
                                        if(lvl == 1){
                                            boots_modifier = prot1;
                                        } else if(lvl == 2){
                                            boots_modifier = prot2;
                                        } else if(lvl == 3){
                                            boots_modifier = prot3;
                                        } else if(lvl == 4){
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
                            setActive(power.getTag(), false);
                        }

                        ticksEMap.put(p, 0);
                    }
                }
            }
        }
    }
    
    @Override
    public void run(Player p) {
//        if (damage_over_time.contains(p)) {
//            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
//                PowerContainer power = origin.getPowerFileFromType("origins:damage_over_time");
//                if (power == null) continue;
//                if (power.getInterval() == null) {
//                    Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.damageOverTime"));
//                    return;
//                }
//                interval = power.getInterval();
//                if (ticksE < interval) {
//                    ticksE++;
//                    return;
//                } else {
//
//                    ticksE = 0;
//                }
//            }
//        }
        //removed old code to use new OriginScheduler
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
