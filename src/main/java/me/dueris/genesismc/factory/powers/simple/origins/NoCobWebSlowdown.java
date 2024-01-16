package me.dueris.genesismc.factory.powers.simple.origins;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.simple.PowerProvider;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class NoCobWebSlowdown extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> cobwebBypassers = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("master_of_webs_no_slowdown");

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void insideBlock(EntityInsideBlockEvent e){
        if(!getPowerArray().contains(e.getEntity())) return;
        if(e.getBlock().getType().equals(Material.COBWEB)){
            e.setCancelled(true);
        }
    }

    @Override
    public String getPowerFile() {
        return null;
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return cobwebBypassers;
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
