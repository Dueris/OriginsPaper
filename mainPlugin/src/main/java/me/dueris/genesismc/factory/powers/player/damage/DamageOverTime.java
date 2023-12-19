package me.dueris.genesismc.factory.powers.player.damage;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.OriginCommandSender;
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

    private String damage_type;
    private Long interval;
    private int damage;

    public DamageOverTime() {
        this.interval = 20L;
        this.damage_type = "minecraft:generic";
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

                        if(power.get("damage_type") != null){
                            this.damage_type = power.get("damage_type");
                        }

                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                            setActive(power.getTag(), true);

                            if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                                String command = "damage {player} {amount} {tag}"
                                    .replace("{player}", p.getName())
                                    .replace("{amount}", String.valueOf(damage)
                                    .replace("{tag}", damage_type));
                                Bukkit.dispatchCommand(new OriginCommandSender(), command);
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
