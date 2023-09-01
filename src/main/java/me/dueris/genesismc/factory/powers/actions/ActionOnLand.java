package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnLand extends CraftPower implements Listener {
    private final double MIN_FALL_DISTANCE = 0.5;

    Player p;

    public ActionOnLand(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void e(PlayerMoveEvent e) {
        if (!getPowerArray().contains(e.getPlayer())) return;
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (e.getFrom().getY() > e.getTo().getY() && e.getFrom().getY() - e.getTo().getY() >= MIN_FALL_DISTANCE) {
                setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                ActionTypes.EntityActionType(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getEntityAction());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
