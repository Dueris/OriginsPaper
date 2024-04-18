package me.dueris.genesismc.screen;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChoosePromptEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.screen.contents.ScreenContent;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.screen.OriginChoosing.choosing;

public class GuiTicker extends BukkitRunnable {
    public static List<Player> delayedPlayers = new ArrayList<>();

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (delayedPlayers.contains(p)) continue;
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                if (layer.testChoosable(p).isEmpty()) continue;
                try {
                    if (OriginPlayerAccessor.hasOrigin(p, CraftApoli.nullOrigin().getTag())) {
                        if (layer.testDefaultOrigin(p)) continue;
                        String openInventoryTitle = p.getOpenInventory().getTitle();
                        if (!openInventoryTitle.startsWith("Choosing Menu") && !openInventoryTitle.startsWith("Custom Origins") && !openInventoryTitle.startsWith("Custom Origin") && !openInventoryTitle.startsWith("Origin")) {
                            OriginChoosePromptEvent event = new OriginChoosePromptEvent(p);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCanceled()) {
                                if (OriginPlayerAccessor.getOrigin(p, layer).getTag().equals(CraftApoli.nullOrigin().getTag())) {
                                    choosing.put(p, layer);
                                    Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu - " + layer.getName());
                                    mainmenu.setContents(ScreenContent.GenesisMainMenuContents(p));
                                    p.openInventory(mainmenu);
                                }
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
