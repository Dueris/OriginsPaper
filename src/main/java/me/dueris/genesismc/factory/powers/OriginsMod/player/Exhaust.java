package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.translation.LangConfig;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Exhaust extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    private Long interval;

    private int ticksE;

    public Exhaust() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (more_exhaustion.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    PowerContainer power = origin.getPowerFileFromType("origins:exhaust");
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
                        ConditionExecutor conditionExecutor = new ConditionExecutor();
                        if (conditionExecutor.check("condition", "conditions", p, origin, "origins:exhaust", null, p)) {
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            p.setExhaustion(p.getExhaustion() - Float.parseFloat(origin.getPowerFileFromType("origins:exhaust").get("exhaustion", "1")));
                        }else{
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
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
