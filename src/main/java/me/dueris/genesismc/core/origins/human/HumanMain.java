package me.dueris.genesismc.core.origins.human;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class HumanMain implements Listener {
    @EventHandler
    public void onSpawn(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (originid == 0004013 || originid == 0) {
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                p.setInvisible(false);
                p.setInvulnerable(false);
                p.setArrowsInBody(0);
                }

    }
}