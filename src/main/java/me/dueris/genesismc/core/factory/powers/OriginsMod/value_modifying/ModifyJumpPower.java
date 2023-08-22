package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_jump;

public class ModifyJumpPower extends ValueModifyingSuperClass implements Listener {
    @EventHandler
    public void run(PlayerJumpEvent e){
        Player p = e.getPlayer();
        if(modify_jump.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_jump", null, p)){
                    for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_jump").getPossibleModifiers("modifier", "modifiers")){
                        Float value = Float.valueOf(modifier.get("value").toString());
                        String operation = modifier.get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(e.getPlayer().getVelocity().getY(), value);
                            e.getPlayer().getVelocity().setY(result);
                        }
                    }

                }
            }
        }
    }
}
