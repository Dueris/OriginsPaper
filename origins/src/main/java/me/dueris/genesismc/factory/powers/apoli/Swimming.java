package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Swimming extends CraftPower {

    @Override
    public void run(Player p, Power power) {
        if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            setActive(p, power.getTag(), false);
            return;
        }
        p.setSwimming(true);
        setActive(p, power.getTag(), true);
    }

    @Override
    public String getType() {
        return "apoli:swimming";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return swimming;
    }
}
