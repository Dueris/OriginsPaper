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

import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_being_used;

public class PreventBeingUsed extends CraftPower implements Listener {
    @EventHandler
    public void run(PlayerInteractEvent e){
        if(prevent_being_used.contains(e.getPlayer())){
            Player p = e.getPlayer();
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("bientity_condition", "bientity_conditions", p, origin, "origins:prevent_being_used", null, p)){
                    if(conditionExecutor.check("item_condition", "item_conditions", p, origin, "origins:prevent_being_used", null, p)){
                        e.setCancelled(true);
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
        return "origins:prevent_being_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_being_used;
    }
}
