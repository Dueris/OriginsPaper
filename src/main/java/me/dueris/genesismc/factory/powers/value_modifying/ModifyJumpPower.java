package me.dueris.genesismc.factory.powers.value_modifying;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.*;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_jump;

public class ModifyJumpPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void run(PlayerJumpEvent e) {
        Player p = e.getPlayer();
        if (modify_jump.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_jump", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                    for (HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_jump").getPossibleModifiers("modifier", "modifiers")) {

                        if(modifier.get("value") instanceof Float){
                            Float value = Float.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            if (mathOperator != null) {
                                float result = (float) mathOperator.apply(p.getVelocity().getY(), value);
                                p.setVelocity(new Vector(p.getEyeLocation().getDirection().getX(), result, p.getEyeLocation().getDirection().getZ()));
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            }
                        }
                        if (modifier.get("value") instanceof Double) {
                            Double value = Double.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
                            if (mathOperator != null) {
                                double result = (double) mathOperator.apply(p.getVelocity().getY(), value);
                                p.setVelocity(new Vector(p.getEyeLocation().getDirection().getX(), result, p.getEyeLocation().getDirection().getZ()));
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            }
                        }
                        if (modifier.get("value") instanceof Integer) {
                            Integer value = Integer.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsInteger().get(operation);
                            if (mathOperator != null) {
                                int result = (int) mathOperator.apply(p.getVelocity().getY(), value);
                                p.setVelocity(new Vector(p.getEyeLocation().getDirection().getX(), result, p.getEyeLocation().getDirection().getZ()));
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            }
                        }
                        if (modifier.get("value") instanceof Long) {
                            Long value = Long.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator<Long> mathOperator = getOperationMappingsLong().get(operation);
                            if (mathOperator != null) {
                                long result = mathOperator.apply((long) p.getVelocity().getY(), value);
                                p.setVelocity(new Vector(p.getEyeLocation().getDirection().getX(), result, p.getVelocity().getZ()));
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_jump";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_jump;
    }
}
