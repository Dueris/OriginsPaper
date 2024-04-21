package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Burn extends CraftPower {

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (!power.isPresent("interval")) {
                        throw new IllegalArgumentException("Interval must not be null! Provide an interval!! : " + power.fillStackTrace());
                    }

                    long interval = power.getNumber("interval").getLong();
                    if (interval == 0) interval = 1L;
                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        if (p.isInWaterOrRainOrBubbleColumn()) return;
                        if (p.getGameMode() == GameMode.CREATIVE) return;
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                            setActive(p, power.getTag(), true);

                            long burn_duration = power.getNumberOrDefault("burn_duration", 100L).getLong();
                            p.setFireTicks((int) burn_duration * 20);
                        } else {
                            setActive(p, power.getTag(), false);
                        }

                    }
                }
            }
        }
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
