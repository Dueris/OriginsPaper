package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_entity_collision;

public class PreventEntityCollision extends CraftPower {

    @Override
    public void run(Player p, Power power) {
        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            p.setCollidable(false);
            setActive(p, power.getTag(), false);
        } else {
            setActive(p, power.getTag(), false);
            p.setCollidable(true);
        }
    }

    @Override
    public void doesntHavePower(Player p) {
        p.setCollidable(true);
    }

    @Override
    public String getType() {
        return "apoli:prevent_entity_collision";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return prevent_entity_collision;
    }
}
