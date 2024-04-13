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

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_sprinting;

public class PreventSprinting extends CraftPower {


    @Override
    public void run(Player p) {
        if (prevent_sprinting.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                        setActive(p, power.getTag(), true);
                        p.setSprinting(false);
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_sprinting";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_sprinting;
    }
}
