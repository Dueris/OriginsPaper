package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void runD(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!modify_healing.contains(e.getEntity())) return;
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_healing").getPossibleModifiers("modifier", "modifiers")) {
                    Float value = Float.valueOf(modifier.get("value").toString());
                    String operation = modifier.get("operation").toString();
                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                    if (mathOperator != null) {
                        float result = (float) mathOperator.apply(e.getAmount(), value);
                        ConditionExecutor executor = new ConditionExecutor();
                        if (executor.check("condition", "conditions", p, origin, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            e.setAmount(result);
                        } else {
                            if (origin.getPowerFileFromType(getPowerFile()) == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                    }
                }
            }
        }
    }

    Player p;

    public ModifyHealingPower(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_healing";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_healing;
    }
}
