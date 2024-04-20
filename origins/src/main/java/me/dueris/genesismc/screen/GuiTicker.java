package me.dueris.genesismc.screen;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChoosePromptEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.screen.ScreenNavigator.inChoosingLayer;

import java.util.ArrayList;
import java.util.List;

public class GuiTicker extends BukkitRunnable {
    public static List<Player> delayedPlayers = new ArrayList<>();

    @Override
    public void run() {
        if (ScreenNavigator.layerPages.isEmpty()) return; // No pages to display.
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (delayedPlayers.contains(p)) continue;
            for (Layer layer : CraftApoli.getLayersFromRegistry().stream().filter(Layer::isEnabled).toList()) {
                if (layer.testChoosable(p).isEmpty()) continue;
                try {
                    if (OriginPlayerAccessor.getOrigin(p, layer).getTag().equalsIgnoreCase("origins:empty")) {
                        if (layer.testDefaultOrigin(p)) continue;
                        if (!inChoosingLayer.containsKey(((CraftPlayer)p).getHandle())) {
                            OriginChoosePromptEvent event = new OriginChoosePromptEvent(p);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCanceled()) {
                                ScreenNavigator.open(p, layer, false);
                            }
                        }
                    }
                    p.setInvulnerable(inChoosingLayer.containsKey(((CraftPlayer)p).getHandle()));
                } catch (Exception e) {
                    p.getPersistentDataContainer().remove(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"));
                    e.printStackTrace();
                }
            }
        }
    }
}
