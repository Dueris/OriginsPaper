package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StackingStatusEffect extends CraftPower {
    public static PotionEffectType getPotionEffectType(String effectString) {
        if (effectString == null) {
            return null;
        }
        return PotionEffectType.getByKey(NamespacedKey.fromString(effectString));
    }

    @Override
    public void run(Player p) {

    }

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);
        if (getPowerArray().contains(p)) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power == null) continue;

                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE < 30) {
                        ticksE++;

                        ticksEMap.put(p, ticksE);
                        return;
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("entity_condition", "entity_conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, null, null)) {
                                setActive(p, power.getTag(), true);
                                applyStackingEffect(p, Integer.parseInt(power.get("duration_per_stack")), origin, power);
                            } else {
                                setActive(p, power.getTag(), false);
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                        ticksEMap.put(p, 0);
                    }
                }
            }
        }
    }

    private void applyStackingEffect(Player player, int stacks, OriginContainer origin, PowerContainer power) {
        for (HashMap<String, Object> effect : power.getSingularAndPlural("effect", "effects")) {
            PotionEffectType potionEffectType = getPotionEffectType(effect.get("effect").toString());
            if (potionEffectType != null) {
                try {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 40, 1, false, false, false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Bukkit.getLogger().warning("Unknown effect ID: " + effect.get("effect").toString());
            }
        }
        player.sendHealthUpdate();
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:stacking_status_effect";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return stacking_status_effect;
    }
}
