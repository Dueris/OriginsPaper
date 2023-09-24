package me.dueris.genesismc.commands.subcommands.origin.Info;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.commands.subcommands.origin.Info.Info.playerOrigins;
import static me.dueris.genesismc.commands.subcommands.origin.Info.Info.playerPage;

public class InInfoCheck extends BukkitRunnable {

    //removes player from info page when menu closed
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getOpenInventory().getTitle().startsWith("Info")) {
                playerPage.remove(player);
                playerOrigins.remove(player);
            }
        }
    }
}
