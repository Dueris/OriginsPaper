package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;

public class DisableRegen extends CraftPower implements Listener {
    @EventHandler
    public void disable(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (disable_regen.contains(p)) {
                if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
                    e.setAmount(0);
                    e.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:disable_regen";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return disable_regen;
    }
}
