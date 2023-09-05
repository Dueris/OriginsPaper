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

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_item_use;

public class PreventItemUse extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    public PreventItemUse(){

    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(PlayerInteractEvent e) {
//        if (prevent_item_use.contains(e.getPlayer())) {
//            if (e.getAction().isRightClick()) {
//                if (e.getItem() == null) return;
//
//                for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
//                    if (origin.getPowerFileFromType(getPowerFile()) == null) {
//                        getPowerArray().remove(e.getPlayer());
//                        return;
//                    } else {
//                        ConditionExecutor conditionExecutor = new ConditionExecutor();
//                        boolean shouldCancel = conditionExecutor.check("item_condition", "item_conditions", e.getPlayer(), origin, "origins:prevent_item_use", e.getPlayer(), null, e.getPlayer().getLocation().getBlock(), null, e.getItem(), null);
//
//                        if (shouldCancel) {
//                            e.setCancelled(true);
//                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
//                        } else {
//                                e.setCancelled(false);
//                                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
//                        }
//                    }
//                }
//            } else {
//                e.getPlayer().sendMessage("sdfsadfsdf");
//                e.setCancelled(false);
//            }
//        }
        //DISABLED FOR NOW DUE TO WEIRD ISSUES WITH ITEM CONDITIONS
        //TODO: WORK ON PATCH
    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_item_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_item_use;
    }
}
