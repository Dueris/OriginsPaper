package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ExhaustPower extends CraftPower {

    @Override
    public void run(Player p, Power power) {
        long interval = power.getNumberOrDefault("interval", 20L).getLong();
        if (p.getTicksLived() % interval == 0) {
            if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                setActive(p, power.getTag(), true);
                if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                    return;
                ((CraftPlayer)p).getHandle().causeFoodExhaustion(power.getNumber("exhaustion").getFloat());
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
