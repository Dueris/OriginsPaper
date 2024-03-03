package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Exhaust extends CraftPower {

    private Long interval;
    private int ticksE;

    public Exhaust() {
        this.interval = 1L;
        this.ticksE = 0;
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

    @Override
    public void run(Player p) {
        if (more_exhaustion.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getObject("interval") == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.exhaust"));
                        return;
                    }
                    interval = power.getLong("interval");
                    if(interval == 0) interval = 1L;
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                            setActive(p, power.getTag(), true);
                            p.setExhaustion(p.getExhaustion() - power.getFloatOrDefault("exhaustion", 1));
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                        ticksE = 0;
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:exhaust";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return more_exhaustion;
    }
}
