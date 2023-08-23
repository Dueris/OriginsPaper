package me.dueris.genesismc.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_elytra_flight;

public class PreventElytraFlight extends CraftPower implements Listener {
    @EventHandler
    public void run(EntityToggleGlideEvent e){
        if(e.getEntity() instanceof Player p){
            if(prevent_elytra_flight.contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", p, origin, "origins:prevent_elytra_flight", null, p)){
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
        return "origins:prevent_elytra_flight";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_elytra_flight;
    }
}
