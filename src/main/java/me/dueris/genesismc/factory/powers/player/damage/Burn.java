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

public class Burn extends CraftPower {

    private Long interval;
    private int ticksE;

    public Burn() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (burn.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    PowerContainer power = origin.getPowerFileFromType("origins:burn");
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.burn"));
                        return;
                    }
                    interval = power.getInterval();
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        if (p.isInWaterOrRainOrBubbleColumn()) return;
                        if (p.getGameMode() == GameMode.CREATIVE) return;
                        ConditionExecutor executor = new ConditionExecutor();
                        if (executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)) {
                            if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);

                            Long burn_duration = power.getBurnDuration();
                            p.setFireTicks(burn_duration.intValue() * 20);
                        } else {
                            if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
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
        return "origins:burn";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return burn;
    }
}
