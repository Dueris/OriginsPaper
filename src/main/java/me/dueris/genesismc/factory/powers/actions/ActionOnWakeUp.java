package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;

public class ActionOnWakeUp extends CraftPower implements Listener {
    Player p;

    public ActionOnWakeUp(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void w(PlayerBedLeaveEvent e) {
        if (!getPowerArray().contains(e.getPlayer())) return;
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            for(PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())){
                setActive(power.getTag(), true);
                ActionTypes.EntityActionType(e.getPlayer(), power.getEntityAction());
                ActionTypes.BlockActionType(e.getBed().getLocation(), power.getBlockAction());
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_wake_up";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_wake_up;
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
