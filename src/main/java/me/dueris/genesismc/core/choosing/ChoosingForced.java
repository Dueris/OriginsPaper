package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;

public class ChoosingForced extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (OriginPlayer.getOrigin(p).getTag().equalsIgnoreCase("genesis:origin-choosing") || OriginPlayer.getOrigin(p).getTag().equalsIgnoreCase("genesis:origin-null")) {
                if (!p.getOpenInventory().getTitle().equalsIgnoreCase("Choosing Menu") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origins") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Expanded Origins") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origin") && !p.getOpenInventory().getTitle().equalsIgnoreCase("Origin")) {
                    @NotNull Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu");
                    mainmenu.setContents(GenesisMainMenuContents(p));
                    p.openInventory(mainmenu);

                }
            }

            if (!p.getScoreboardTags().contains("chosen")) {
                p.addScoreboardTag("choosing");
            }
            p.setInvulnerable(p.getOpenInventory().getTitle().equalsIgnoreCase("Choosing Menu") || p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origins") || p.getOpenInventory().getTitle().equalsIgnoreCase("Expanded Origins") || p.getOpenInventory().getTitle().equalsIgnoreCase("Custom Origin"));
        }
    }
}
