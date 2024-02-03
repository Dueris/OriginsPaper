package me.dueris.genesismc.factory.powers.block;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

@Deprecated // TODO: finish this power
public class Recipe extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:recipe";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return recipe;
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
