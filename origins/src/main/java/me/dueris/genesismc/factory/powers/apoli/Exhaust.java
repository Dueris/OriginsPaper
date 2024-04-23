package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Exhaust extends CraftPower {

    @Override
    public void run(Player p, Power power) {
        if (!power.isPresent("interval")) {
            throw new IllegalArgumentException("Interval must not be null! Provide an interval!! : " + power.fillStackTrace());
        }
        long interval = power.getNumberOrDefault("interval", 20L).getLong();
        if (Bukkit.getServer().getCurrentTick() % interval == 0) {
            if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                setActive(p, power.getTag(), true);
                if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                    return;
                if (Math.round(p.getFoodLevel() - power.getNumberOrDefault("exhaustion", 1).getFloat()) <= 0)
                    p.setFoodLevel(0);
                else
                    p.setFoodLevel(Math.round(p.getFoodLevel() - power.getNumberOrDefault("exhaustion", 1).getFloat()));
            } else {
                setActive(p, power.getTag(), false);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:exhaust";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return more_exhaustion;
    }
}
