package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOverTime extends CraftPower {

    private final int ticksE;
    private Long interval;

    public ActionOverTime() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);

        if (getPowerArray().contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;

                    interval = power.getLongOrDefault("interval", 20L);
                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE <= interval) {
                        ticksE++;
                        ticksEMap.put(p, ticksE);
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            setActive(p, power.getTag(), true);
                            Actions.EntityActionType(p, power.getEntityAction());
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                        ticksEMap.put(p, 0);
                    }
                }
            }
        }
    }


    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:action_over_time";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_ove_time;
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
