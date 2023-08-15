package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.DisableRegen;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.skinsrestorer.api.serverinfo.Platform;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_healing;

public class ModifyHealingPower implements Listener {
    @EventHandler
    public void run(EntityRegainHealthEvent e){
        if(e.getEntity() instanceof Player){
            if(!modify_healing.contains(e.getEntity())) return;
            Player p = (Player) e.getEntity();
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_healing").getModifier().get("value").toString());
                String operation = origin.getPowerFileFromType("origins:modify_healing").getModifier().get("operation").toString();
                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                if (mathOperator != null) {
                    float result = (float) mathOperator.apply(e.getAmount(), value);
                    e.setAmount(result);
                }
            }
        }
    }
}
