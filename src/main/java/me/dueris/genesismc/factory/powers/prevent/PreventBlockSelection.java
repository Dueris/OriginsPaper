package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_block_selection;

public class PreventBlockSelection extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void runI(PlayerInteractEvent e) {
        if (prevent_block_selection.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (e.getClickedBlock() != null) e.setCancelled(true);
                if (conditionExecutor.check("block_condition", "block_condition", e.getPlayer(), origin, "origins:prevent_block_selection", e.getPlayer(), null, e.getClickedBlock(), null, e.getPlayer().getItemInHand(), null)) {
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                } else {
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    Player p;

    public PreventBlockSelection(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

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
