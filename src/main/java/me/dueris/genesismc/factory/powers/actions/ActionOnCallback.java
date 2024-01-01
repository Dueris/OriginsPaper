package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnCallback extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void choose(OriginChangeEvent e){
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
                if (power == null) continue;
                    setActive(e.getPlayer(), power.getTag(), true);
                    Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                    Actions.EntityActionType(e.getPlayer(), power.getAction("entity_action_chosen"));
                    Actions.ItemActionType(e.getPlayer().getActiveItem(), power.getItemAction());
                    Actions.ItemActionType(e.getPlayer().getActiveItem(), power.getAction("held_item_action"));
                    Actions.ItemActionType(e.getPlayer().getActiveItem(), power.getAction("result_item_action"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setActive(e.getPlayer(), power.getTag(), false);
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_callback";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_callback;
    }

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
}
