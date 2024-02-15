package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_xp_gain;

public class ModifyExperienceGainPower extends CraftPower implements Listener {

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

    }

    @EventHandler
    public void run(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        if (modify_xp_gain.contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("condition", "conditions", p, power, "apoli:modify_xp_gain", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            for (HashMap<String, Object> modifier : power.getJsonListSingularPlural("modifier", "modifiers")) {
                                Float value = Float.valueOf(modifier.get("value").toString());
                                String operation = modifier.get("operation").toString();
                                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    float result = (float) mathOperator.apply(e.getAmount(), value);
                                    e.setAmount(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                                    if (power == null) {
                                        getPowerArray().remove(p);
                                        return;
                                    }
                                    if (!getPowerArray().contains(p)) return;
                                    setActive(p, power.getTag(), true);
                                }
                            }

                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ev.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_xp_gain";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_xp_gain;
    }
}
