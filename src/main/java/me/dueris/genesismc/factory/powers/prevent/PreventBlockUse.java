package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_block_use;

public class PreventBlockUse extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void run(PlayerInteractEvent e) {
        if (prevent_block_use.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (e.getClickedBlock() != null) e.setCancelled(true);
                if (conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_block_used", e.getPlayer(), null, e.getClickedBlock(), null, e.getPlayer().getItemInHand(), null)) {
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                } else {
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @EventHandler
    public void run(BlockPlaceEvent e) {
        if (prevent_block_use.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), origin, "origins:prevent_block_used", e.getPlayer(), null, e.getBlock(), null, e.getPlayer().getItemInHand(), null)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    Player p;

    public PreventBlockUse(){
        this.p = p;
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
