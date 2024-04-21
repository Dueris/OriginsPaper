package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Freeze extends CraftPower {

    @Override
    public void run(Player p, Power power) {
        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            setActive(p, power.getTag(), true);
            p.setFreezeTicks(300);
        } else {
            setActive(p, power.getTag(), false);
        }
    }

    @Override
    public String getType() {
        return "apoli:freeze";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return freeze;
    }
}
