package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnCallback extends CraftPower implements Listener {
    Player p;

    public ActionOnCallback() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void choose(OriginChangeEvent e){
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (OriginContainer origin : OriginPlayer.getOrigin(actor).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (power == null) continue;
                            if (!getPowerArray().contains(e.getPlayer())) return;
                            setActive(power.getTag(), true);
                            Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                            Actions.EntityActionType(e.getPlayer(), power.getAction("entity_action_chosen"));
                            Actions.ItemActionType(e.getPlayer().getActiveItem(), power.getItemAction());
                            Actions.ItemActionType(e.getPlayer().getActiveItem(), power.getAction("held_item_action"));
                            Actions.ItemActionType(e.getPlayer().getActiveItem(), power.getAction("result_item_action"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!getPowerArray().contains(e.getPlayer())) return;
                                    setActive(power.getTag(), false);
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
