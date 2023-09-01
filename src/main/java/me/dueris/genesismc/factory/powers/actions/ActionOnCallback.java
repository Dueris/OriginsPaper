package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ActionOnCallback extends CraftPower implements Listener {
    Player p;

    public ActionOnCallback(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

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
