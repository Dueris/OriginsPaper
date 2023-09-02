package me.dueris.genesismc.factory.powers.player.damage;

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

public class Burn extends CraftPower {

    private Long interval;

    public Burn() {
        this.interval = 1L;
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

        if (burn.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                PowerContainer power = origin.getPowerFileFromType("origins:burn");
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
                    if (p.isInWaterOrRainOrBubbleColumn()) return;
                    if (p.getGameMode() == GameMode.CREATIVE) return;
                    ConditionExecutor executor = new ConditionExecutor();
                    if (executor.check("condition", "conditions", p, origin, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);

                        Long burn_duration = power.getBurnDuration();
                        p.setFireTicks(burn_duration.intValue() * 20);
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
        return "origins:burn";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return burn;
    }
}
