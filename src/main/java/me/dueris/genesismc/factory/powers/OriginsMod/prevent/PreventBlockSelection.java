package me.dueris.genesismc.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_block_selection;

public class PreventBlockSelection extends CraftPower implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(prevent_block_selection.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("block_condition", "block_condition", e.getPlayer(), origin, "origins:prevent_block_selection", null, e.getPlayer())){
                    if(e.getClickedBlock() != null) e.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_block_selection";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_block_selection;
    }
}
