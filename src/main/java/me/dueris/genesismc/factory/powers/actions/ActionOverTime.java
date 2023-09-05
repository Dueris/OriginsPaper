package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOverTime extends CraftPower {

    private Long interval;
    private int ticksE;

    public ActionOverTime() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);

        if (getPowerArray().contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                PowerContainer power = origin.getPowerFileFromType(getPowerFile());

                if (power == null) continue;
                if (power.getInterval() == null) {
                    Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.action_over_time"));
                    return;
                }

                interval = power.getInterval();
                int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE <= interval) {
                        ticksE++;
                        ticksEMap.put(p, ticksE);
                        p.sendMessage(String.valueOf(ticksE));
                    } else {
                        ConditionExecutor executor = new ConditionExecutor();
                        if (executor.check("condition", "conditions", p, origin, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            ActionTypes.EntityActionType(p, power.getEntityAction());
                        } else {
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                        ticksEMap.put(p, 0);
                    }
            }
        }
    }



    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:action_over_time";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_ove_time;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
