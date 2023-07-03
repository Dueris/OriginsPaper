package me.dueris.genesismc.core.factory.powers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.creative_flight;

public class FlightHandler extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (creative_flight.contains(p)) {
                p.setAllowFlight(true);
            } else {
                if (p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.setAllowFlight(true);
                } else p.setAllowFlight(p.getGameMode().equals(GameMode.SPECTATOR));
            }
        }
    }
}
