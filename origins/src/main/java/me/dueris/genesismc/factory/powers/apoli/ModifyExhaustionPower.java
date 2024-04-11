package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_exhaustion;

public class ModifyExhaustionPower extends CraftPower implements Listener {


    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void run(EntityExhaustionEvent e) {
        Player p = (Player) e.getEntity();
        if (modify_exhaustion.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        for (HashMap<String, Object> modifier : power.getJsonListSingularPlural("modifier", "modifiers")) {
                            Float value = Float.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            if (mathOperator != null) {
                                float result = (float) mathOperator.apply(e.getExhaustion(), value);
                                e.setExhaustion(result);

                                setActive(p, power.getTag(), true);
                            }
                        }

                    } else {

                        setActive(p, power.getTag(), false);
                    }
                }

            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_exhaustion";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_exhaustion;
    }
}
