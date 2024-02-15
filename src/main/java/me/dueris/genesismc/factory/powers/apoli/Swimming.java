package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Swimming extends CraftPower {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {
        for (LayerContainer layer : CraftApoli.getLayers()) {
            if (swimming.contains(p)) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    if (!conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {

                        setActive(p, power.getTag(), false);
                        return;
                    } else {
                        p.setSwimming(true);

                        setActive(p, power.getTag(), true);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:swimming";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return swimming;
    }
}
