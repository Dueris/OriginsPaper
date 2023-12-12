package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_entity_collision;

public class PreventEntityCollision extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public void run(Player p) {
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (prevent_entity_collision.contains(p)) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("bientity_condition", "bientity_condition", p, power, "origins:prevent_entity_collision", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        p.setCollidable(false);
                        if (power == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(power.getTag(), false);
                    } else {
                        if (power == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(power.getTag(), false);
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
