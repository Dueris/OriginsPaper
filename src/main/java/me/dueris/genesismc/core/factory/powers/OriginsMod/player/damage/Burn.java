package me.dueris.genesismc.core.factory.powers.OriginsMod.player.damage;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.burn;

public class Burn extends BukkitRunnable {

    private Long interval;

    private int ticksE;

    public Burn() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (burn.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    PowerContainer power = origin.getPowerFileFromType("origins:burn");
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.burn"));
                        return;
                    }
                    interval = power.getInterval();
                    if (ticksE < interval) {
                        ticksE++;
                        return;
                    } else {
                        if (p.isInWaterOrRainOrBubbleColumn()) return;
                        if (p.getGameMode() == GameMode.CREATIVE) return;
                        ConditionExecutor conditionExecutor = new ConditionExecutor();
                        if (!conditionExecutor.check("condition", "conditions", p, origin, "origins:burn", null, p)) return;
                        Long burn_duration = power.getBurnDuration();
                        p.setFireTicks(burn_duration.intValue());

                        ticksE = 0;
                    }
                }
            }
        }
    }
}
