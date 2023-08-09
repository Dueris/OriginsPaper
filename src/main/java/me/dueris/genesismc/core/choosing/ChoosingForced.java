package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.LayerContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.choosing;
import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;

public class ChoosingForced extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (OriginPlayer.hasOrigin(p, CraftApoli.nullOrigin().getTag())) {
                if (!p.getOpenInventory().getTitle().startsWith("Choosing Menu") && !p.getOpenInventory().getTitle().startsWith("Custom Origins") && !p.getOpenInventory().getTitle().startsWith("Expanded Origins") && !p.getOpenInventory().getTitle().startsWith("Custom Origin") && !p.getOpenInventory().getTitle().startsWith("Origin")) {
                    for (LayerContainer layer : CraftApoli.getLayers()) {
                        if (OriginPlayer.getOrigin(p, layer).getTag().equals(CraftApoli.nullOrigin().getTag())) {
                            choosing.put(p, layer);
                            @NotNull Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                            mainmenu.setContents(GenesisMainMenuContents(p));
                            p.openInventory(mainmenu);
                        }
                    }
                }
            }
            p.setInvulnerable(p.getOpenInventory().getTitle().startsWith("Choosing Menu") || p.getOpenInventory().getTitle().startsWith("Custom Origins") || p.getOpenInventory().getTitle().startsWith("Expanded Origins") || p.getOpenInventory().getTitle().startsWith("Custom Origin"));
        }
    }
}
