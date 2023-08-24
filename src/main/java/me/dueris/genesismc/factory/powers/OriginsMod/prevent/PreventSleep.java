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

import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_sleep;
import static me.dueris.genesismc.factory.powers.entity.FreshAir.beds;

public class PreventSleep extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    public void run(PlayerInteractEvent e){
        if(e.getClickedBlock() == null) return;
        if(beds.contains(e.getClickedBlock().getType())){
            if(!prevent_sleep.contains(e.getPlayer())) return;
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_sleep", null, e.getPlayer())){
                    if(origin.getPowerFileFromType("origins:prevent_sleep").get("set_spawn_point", "false").toString() == "true"){
                        e.getPlayer().setBedSpawnLocation(e.getClickedBlock().getLocation());
                    }
                    if(origin.getPowerFileFromType("origins:prevent_sleep").get("message", "origins.cant_sleep") != null){
                        e.getPlayer().sendMessage(origin.getPowerFileFromType("origins:prevent_sleep").get("message", "origins.cant_sleep"));
                    }
                    if(!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    e.setCancelled(true);
                }else{
                    if(!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_sleep";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_sleep;
    }
}
