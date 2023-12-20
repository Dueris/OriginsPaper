package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Exhaust extends CraftPower {

    private Long interval;
    private int ticksE;

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

    public Exhaust() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void run(Player p) {
        if (more_exhaustion.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.exhaust"));
                        return;
                    }
                    interval = power.getInterval();
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (conditionExecutor.check("condition", "conditions", p, power, "origins:exhaust", p, null, null, null, p.getItemInHand(), null)) {
                            if (power == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(p, power.getTag(), true);
                            p.setExhaustion(p.getExhaustion() - Float.parseFloat(power.get("exhaustion", "1")));
                        } else {
                            if (power == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
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
        return "origins:exhaust";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return more_exhaustion;
    }
}
