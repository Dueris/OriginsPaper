package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_jump;

public class ModifyJumpPower implements Listener {
    @EventHandler
    public void run(PlayerJumpEvent e){
        Player p = e.getPlayer();
        if(modify_jump.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(ConditionExecutor.check("condition", p, origin, "origins:modify_jump", null, p)){
                    Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_jump").getModifier().get("value").toString());
                    String operation = origin.getPowerFileFromType("origins:modify_jump").getModifier().get("operation").toString();
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
