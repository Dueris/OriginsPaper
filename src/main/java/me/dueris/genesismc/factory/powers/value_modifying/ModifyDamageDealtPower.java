package me.dueris.genesismc.factory.powers.value_modifying;

import com.sun.jna.platform.win32.OaIdl;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.*;
import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsLong;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_damage_dealt;

public class ModifyDamageDealtPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public ModifyDamageDealtPower(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void damageEVENT(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && modify_damage_dealt.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                try {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:modify_damage_dealt", p, e.getEntity(), p.getLocation().getBlock(), null, p.getItemInHand(), e)) {
                        if (conditionExecutor.check("condition", "condition", p, origin, "origins:modify_damage_dealt", p, e.getEntity(), p.getLocation().getBlock(), null, p.getItemInHand(), e)) {
                            for (HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_damage_dealt").getConditionFromString("modifier", "modifiers")) {
                                Object value = modifier.get("value");
                                String operation = modifier.get("operation").toString();
                                runSetDMG(e, operation, value);
                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            }
                        }
                    } else {
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                } catch (Exception ev) {
                    throw new RuntimeException();
                }
            }
        }
    }

    public void runSetDMG(EntityDamageByEntityEvent e, String operation, Object value) {
        double damage = e.getDamage();

        if (value instanceof Double) {
            BinaryOperator<Double> doubleOperator = getOperationMappingsDouble().get(operation);
            if (doubleOperator != null) {
                double newDamage = doubleOperator.apply(damage, (Double) value);
                e.setDamage(newDamage);
            }
        } else if (value instanceof Long) {
            BinaryOperator<Long> longOperator = getOperationMappingsLong().get(operation);
            if (longOperator != null) {
                long newDamage = longOperator.apply((long) damage, (Long) value);
                e.setDamage(newDamage);
            }
        } else if (value instanceof Integer) {
            BinaryOperator<Integer> intOperator = getOperationMappingsInteger().get(operation);
            if (intOperator != null) {
                int newDamage = intOperator.apply((int) damage, (Integer) value);
                e.setDamage(newDamage);
            }
        } else if (value instanceof Float) {
            BinaryOperator<Float> floatOperator = getOperationMappingsFloat().get(operation);
            if (floatOperator != null) {
                float newDamage = floatOperator.apply((float) damage, (Float) value);
                e.setDamage(newDamage);
            }
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + value.getClass());
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_damage_dealt";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_damage_dealt;
    }
}
