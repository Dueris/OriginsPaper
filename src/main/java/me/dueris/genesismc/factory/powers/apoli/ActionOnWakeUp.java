package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnWakeUp extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void w(PlayerBedLeaveEvent e) {
        if (!getPowerArray().contains(e.getPlayer())) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                setActive(e.getPlayer(), power.getTag(), true);
                Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                Actions.BlockActionType(e.getBed().getLocation(), power.getBlockAction());
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_wake_up";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_wake_up;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
