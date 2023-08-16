package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.ErrorSystem;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_projectile_damage;

public class ModifyProjectileDamagePower implements Listener {
    @EventHandler
    public void run(EntityDamageEvent e){
        if(e.getEntity() instanceof Player p){
            if(modify_projectile_damage.contains(p)){
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                    try {
                        if (ConditionExecutor.check("target_condition", p, origin, "origins:modify_projectile_damage", e, e.getEntity()) && ConditionExecutor.check("damage_condition", p, origin, "origins:modify_projectile_damage", e, e.getEntity())) {
                            Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_projectile_damage").getModifier().get("value").toString());
                            String operation = origin.getPowerFileFromType("origins:modify_projectile_damage").getModifier().get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            if (mathOperator != null) {
                                float result = (float) mathOperator.apply(e.getDamage(), value);
                                e.setDamage(result);
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
}
