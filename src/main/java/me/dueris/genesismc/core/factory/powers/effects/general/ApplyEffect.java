package me.dueris.genesismc.core.factory.powers.effects.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.apply_effect;

public class ApplyEffect extends BukkitRunnable implements Listener {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (apply_effect.contains(p)) {

            }
        }
    }
}
