package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_block_use;

public class PreventBlockUse extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }


    @EventHandler
    public void run(PlayerInteractEvent e) {
        if (prevent_block_use.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                if (e.getClickedBlock() != null) e.setCancelled(true);
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    setActive(e.getPlayer(), power.getTag(), conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), power, "origins:prevent_block_used", e.getPlayer(), null, e.getClickedBlock(), null, e.getPlayer().getItemInHand(), null));
                }
            }
        }
    }

    @EventHandler
    public void run(BlockPlaceEvent e) {
        if (prevent_block_use.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), power, "origins:prevent_block_used", e.getPlayer(), null, e.getBlock(), null, e.getPlayer().getItemInHand(), null)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_block_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_block_use;
    }
}
