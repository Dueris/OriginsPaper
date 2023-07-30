package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.burn;
import static me.dueris.genesismc.core.factory.powers.Powers.more_exhaustion;

public class Exhaust extends BukkitRunnable {

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
                        Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.exhaust"));
                        return;
                    }
                    interval = power.getInterval();
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        if(ConditionExecutor.check(p, origin, "origins:exhaust", null, p)){
                            p.setExhaustion(p.getExhaustion() - Float.parseFloat(origin.getPowerFileFromType("origins:exhaust").get("exhaustion")));
                        }
                        ticksE = 0;
                    }
                }
            }
        }
    }
}
