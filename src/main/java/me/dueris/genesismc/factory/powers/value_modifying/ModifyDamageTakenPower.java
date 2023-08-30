package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.*;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_damage_taken;

public class ModifyDamageTakenPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void dmgEvent(EntityDamageEvent e){
        if (e.getEntity() instanceof Player p) {
            if (getPowerArray().contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    try {
                        ConditionExecutor conditionExecutor = new ConditionExecutor();
                        if (conditionExecutor.check("damage_condition", "damage_conditions", p, origin, "origins:modify_damage_taken", e, e.getEntity())) {
                            for (HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_damage_taken").getConditionFromString("modifier", "modifiers")) {
                                if(modifier.get("value") instanceof Float){
                                    Float value = Float.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                    if (mathOperator != null) {
                                        p.sendMessage(String.valueOf(e.getDamage()));
                                        float result = (float) mathOperator.apply(e.getDamage(), value);
                                        e.setDamage(result);
                                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                    }
                                } else if (modifier.get("value") instanceof Double) {
                                    Double value = Double.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null) {
                                        double result = (double) mathOperator.apply(e.getDamage(), value);
                                        e.setDamage(result);
                                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                    }
                                } else if (modifier.get("value") instanceof Integer) {
                                    Integer value = Integer.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsInteger().get(operation);
                                    if (mathOperator != null) {
                                        int result = (int) mathOperator.apply(e.getDamage(), value);
                                        e.setDamage(result);
                                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                    }
                                } else if (modifier.get("value") instanceof Long) {
                                    Long value = Long.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsLong().get(operation);
                                    if (mathOperator != null) {
                                        long result = (long) mathOperator.apply(e.getDamage(), value);
                                        e.setDamage(result);
                                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                    }
                                }
                            }

                        } else {
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                    } catch (Exception ev) {
                        ErrorSystem errorSystem = new ErrorSystem();
                        errorSystem.throwError("unable to get bi-entity", "origins:modify_damage_taken", p, origin, OriginPlayer.getLayer(p, origin));
                        ev.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void damageEVENT(EntityDamageByEntityEvent e) {

    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_damage_taken";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_damage_taken;
    }
}
