package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.bow_nope;

public class BowInability implements Listener {

    @EventHandler
    public void onUseBow(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (bow_nope.contains(origintag)) {
            if (e.getItem() != null) {
                if (e.getItem().getType().equals(Material.BOW)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}

