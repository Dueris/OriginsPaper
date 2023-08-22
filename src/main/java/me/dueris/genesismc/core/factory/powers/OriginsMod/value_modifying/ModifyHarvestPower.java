package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_harvest;

public class ModifyHarvestPower implements Listener {
    @EventHandler
    public void run(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(modify_harvest.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("block_condition", "block_conditions", p, origin, "origins:modify_harvest", null, p)){
                    if(origin.getPowerFileFromType("origins:modify_harvest").get("allow", null) == "true"){
                        e.setDropItems(false);
                    }
                }
            }
        }
    }
}
