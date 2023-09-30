package me.dueris.genesismc.choosing;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.choosing.ChoosingCORE.choosing;
import static me.dueris.genesismc.choosing.contents.MainMenuContents.GenesisMainMenuContents;

public class ChoosingGUI extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                try {
                    if (OriginPlayer.hasOrigin(p, CraftApoli.nullOrigin().getTag())) {
                        String openInventoryTitle = p.getOpenInventory().getTitle();
                        if (!openInventoryTitle.startsWith("Choosing Menu") && !openInventoryTitle.startsWith("Custom Origins") && !openInventoryTitle.startsWith("Custom Origin") && !openInventoryTitle.startsWith("Origin")) {
                                if (OriginPlayer.getOrigin(p, layer).getTag().equals(CraftApoli.nullOrigin().getTag())) {
                                    choosing.put(p, layer);
                                    Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                                    mainmenu.setContents(GenesisMainMenuContents(p));
                                    p.openInventory(mainmenu);
                                }
                        }
                    }

                    String openInventoryTitle = p.getOpenInventory().getTitle();
                    p.setInvulnerable(openInventoryTitle.startsWith("Origin - ") || openInventoryTitle.startsWith("Choosing Menu") || openInventoryTitle.startsWith("Custom Origins") || openInventoryTitle.startsWith("Expanded Origins") || openInventoryTitle.startsWith("Custom Origin"));
                } catch (Exception e) {
                    p.getPersistentDataContainer().remove(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"));
                }
            }
        }
    }
}
