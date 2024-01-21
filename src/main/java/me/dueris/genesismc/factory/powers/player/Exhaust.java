package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
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
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getObject("interval") == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.exhaust"));
                        return;
                    }
                    interval = power.getLong("interval");
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (conditionExecutor.check("condition", "conditions", p, power, "apoli:exhaust", p, null, null, null, p.getItemInHand(), null)) {

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
