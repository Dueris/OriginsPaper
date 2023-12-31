package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.PlayerHitGroundEvent;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
public class ActionOnLand extends CraftPower implements Listener {
    private final double MIN_FALL_DISTANCE = 0.5;

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void e(PlayerMoveEvent e) {
        if (!getPowerArray().contains(e.getPlayer())) return;
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            if (e.getFrom().getY() > e.getTo().getY() && e.getFrom().getY() - e.getTo().getY() >= MIN_FALL_DISTANCE) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    setActive(e.getPlayer(), power.getTag(), true);
                    Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setActive(e.getPlayer(), power.getTag(), false);
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 2L);
                }

            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_land";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_land;
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
