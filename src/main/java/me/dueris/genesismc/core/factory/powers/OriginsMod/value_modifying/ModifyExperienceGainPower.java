package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.ErrorSystem;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_damage_dealt;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_xp_gain;

public class ModifyExperienceGainPower implements Listener {
    @EventHandler
    public void run(PlayerExpChangeEvent e){
        Player p = (Player) e.getPlayer();
        if(modify_xp_gain.contains(p)){
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    if (ConditionExecutor.check("condition", p, origin, "origins:modify_xp_gain", null, e.getPlayer())) {
                        Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_xp_gain").getModifier().get("value").toString());
                        String operation = origin.getPowerFileFromType("origins:modify_xp_gain").getModifier().get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(e.getAmount(), value);
                            e.setAmount(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                        }
                    }
                } catch (Exception ev) {
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("idk what errrored lol", "origins:modify_xp_gain", p, origin, OriginPlayer.getLayer(p, origin));
                    ev.printStackTrace();
                }
            }
        }
    }
}
