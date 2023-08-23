package me.dueris.genesismc.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_harvest;

public class ModifyHarvestPower extends CraftPower implements Listener {
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

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_harvest";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_harvest;
    }
}
