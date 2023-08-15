package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_exhaustion;

public class ModifyExhaustionPower implements Listener {
    @EventHandler
    public void run(EntityExhaustionEvent e){
        Player p = (Player) e.getEntity();
        if(modify_exhaustion.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(ConditionExecutor.check("condition", p, origin, "origins:modify_exhaustion", null, p)){
                    Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_damage_taken").getModifier().get("value").toString());
                    String operation = origin.getPowerFileFromType("origins:modify_damage_taken").getModifier().get("operation").toString();
                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                    if (mathOperator != null) {
                        float result = (float) mathOperator.apply(e.getExhaustion(), value);
                        e.setExhaustion(result);
                    }
                }
            }
        }
    }
}
