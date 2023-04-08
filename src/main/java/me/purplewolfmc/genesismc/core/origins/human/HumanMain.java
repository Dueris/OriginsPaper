package me.purplewolfmc.genesismc.core.origins.human;

import me.purplewolfmc.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class HumanMain implements Listener {
    @EventHandler
    public void onSpawn(PlayerMoveEvent e) {
        Player p = (Player) e.getPlayer();

        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 0004013) {
            p.setHealthScale(20);
                p.setMaximumAir(20);
                p.setInvisible(false);
                p.setInvulnerable(false);
                p.setArrowsInBody(0);
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                }

    }
}