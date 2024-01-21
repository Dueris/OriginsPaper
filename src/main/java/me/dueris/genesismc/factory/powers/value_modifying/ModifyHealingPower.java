package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_healing;

public class ModifyHealingPower extends CraftPower implements Listener {

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


    @EventHandler
    public void runD(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!modify_healing.contains(p)) return;
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                        Float value = Float.valueOf(modifier.get("value").toString());
                        String operation = modifier.get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(e.getAmount(), value);
                            ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                            if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {

                                setActive(p, power.getTag(), true);
                                e.setAmount(result);
                            } else {

                                setActive(p, power.getTag(), false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_healing";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_healing;
    }
}
