package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.EnumSet;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_sleep;
import static org.bukkit.Material.*;

public class PreventSleep extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    public static EnumSet<Material> beds;

    static {
        beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
                CYAN_BED, LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
    }

    @EventHandler
    public void run(PlayerInteractEvent e){
        if(e.getClickedBlock() == null) return;
        if(beds.contains(e.getClickedBlock().getType())){
            if(!prevent_sleep.contains(e.getPlayer())) return;
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_sleep", null, e.getPlayer())){
                    if(origin.getPowerFileFromType("origins:prevent_sleep").get("set_spawn_point", "false") == "true"){
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
