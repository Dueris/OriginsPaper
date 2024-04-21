package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class NoCobWebSlowdown extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> cobwebBypassers = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("master_of_webs_no_slowdown");

    @EventHandler
    public void insideBlock(EntityInsideBlockEvent e) {
        if (!getPlayersWithPower().contains(e.getEntity())) return;
        if (e.getBlock().getType().equals(Material.COBWEB)) {
            e.setCancelled(true);
        }
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return cobwebBypassers;
    }

}
