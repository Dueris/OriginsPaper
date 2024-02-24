package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Invisibility extends CraftPower {
    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
            boolean shouldSetInvisible = false;

            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        shouldSetInvisible = true;
                        break;
                    }
                }
            }

            p.setInvisible(shouldSetInvisible || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
        } else {
            if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) {
                return;
            }
            p.setInvisible(false);
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:invisibility";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return invisibility;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
