package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_entity_collision;

public class PreventEntityCollision extends CraftPower {

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
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            if (prevent_entity_collision.contains(p)) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("bientity_condition", "bientity_condition", p, power, "origins:prevent_entity_collision", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        p.setCollidable(false);
                        if (power == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(p, power.getTag(), false);
                    } else {
                        if (power == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
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
        return "origins:prevent_entity_collision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_collision;
    }
}
