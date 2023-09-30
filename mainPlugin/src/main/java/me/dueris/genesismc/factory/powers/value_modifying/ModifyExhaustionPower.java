package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_exhaustion;

public class ModifyExhaustionPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public ModifyExhaustionPower() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void run(EntityExhaustionEvent e) {
        Player p = (Player) e.getEntity();
        if (modify_exhaustion.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "origins:modify_exhaustion", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        for (HashMap<String, Object> modifier : power.getConditionFromString("modifier", "modifiers")) {
                            Float value = Float.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            if (mathOperator != null) {
                                float result = (float) mathOperator.apply(e.getExhaustion(), value);
                                e.setExhaustion(result);
                                if (power == null) {
                                    getPowerArray().remove(p);
                                    return;
                                }
                                if (!getPowerArray().contains(p)) return;
                                setActive(power.getTag(), true);
                            }
                        }

                    } else {

                        setActive(power.getTag(), false);
                    }
                }

            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_exhaustion";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_exhaustion;
    }
}
