package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
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

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);
        if (getPowerArray().contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getObject("interval") == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.burn"));
                        return;
                    }
                    interval = power.getLong("interval");

                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE < interval) {
                        ticksE++;

                        ticksEMap.put(p, ticksE);
                        return;
                    } else {
                        if (p.isInWaterOrRainOrBubbleColumn()) return;
                        if (p.getGameMode() == GameMode.CREATIVE) return;
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            setActive(p, power.getTag(), true);

                            Long burn_duration = power.getLongOrDefault("burn_duration", 100L);
                            p.setFireTicks(burn_duration.intValue() * 20);
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
        return "apoli:burn";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return burn;
    }
}
