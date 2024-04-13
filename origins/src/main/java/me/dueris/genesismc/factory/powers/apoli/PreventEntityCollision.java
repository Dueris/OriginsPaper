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

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_entity_collision;

public class PreventEntityCollision extends CraftPower {


    @Override
    public void run(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (prevent_entity_collision.contains(p)) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                        p.setCollidable(false);
                        setActive(p, power.getTag(), false);
                    } else {
                        setActive(p, power.getTag(), false);
                        p.setCollidable(true);
                    }
                }
            } else {
                p.setCollidable(true);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_entity_collision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_collision;
    }
}
