package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_projectile_damage;

public class ModifyProjectileDamagePower extends CraftPower implements Listener {

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

    }

    @EventHandler
    public void runD(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (modify_projectile_damage.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                    try {
                        ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                            if (conditionExecutor.check("target_condition", "target_conditions", p, power, "origins:modify_projectile_damage", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null) && conditionExecutor.check("damage_condition", "damage_conditions", p, power, "origins:modify_projectile_damage", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                    Float value = Float.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                    if (mathOperator != null) {
                                        float result = (float) mathOperator.apply(e.getDamage(), value);
                                        e.setDamage(result);
                                        setActive(power.getTag(), true);
                                    }
                                }
                            } else {
                                if (power == null) {
                                    getPowerArray().remove(p);
                                    return;
                                }
                                if (!getPowerArray().contains(p)) return;
                                setActive(power.getTag(), false);
                            }
                        }
                    } catch (Exception ev) {
                        ErrorSystem errorSystem = new ErrorSystem();
                        errorSystem.throwError("unable to get bi-entity", "origins:modify_projectile_damage", p, origin, OriginPlayer.getLayer(p, origin));
                        ev.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_projectile_damage";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_projectile_damage;
    }
}
