package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ActionOnCallback extends CraftPower implements Listener {
    @Override
    public void run() {

    }

//    @EventHandler
//    public void event(PlayerEvent e){
//        Player actor = e.getPlayer();
//
//        if (!getPowerArray().contains(actor)) return;
//
//        for (OriginContainer origin : OriginPlayer.getOrigin(actor).values()) {
//            PowerContainer power = origin.getPowerFileFromType(getPowerFile());
//            if (power == null) continue;
//            //todo: item block and entity condition
//            if(!getPowerArray().contains(e.getPlayer())) return;
//            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
//            ActionTypes.EntityActionType(e.getPlayer(), power.getEntityAction());
//            //todo:make more events for it, see origins.readthedocs.io/en/latest/power_types/action_on_callback
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if(!getPowerArray().contains(e.getPlayer())) return;
//                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
//                }
//            }.runTaskLater(GenesisMC.getPlugin(), 2L);
//        }
//    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_callback";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_callback;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
