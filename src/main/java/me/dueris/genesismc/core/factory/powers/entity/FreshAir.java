package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.fresh_air;

public class FreshAir implements Listener {

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (fresh_air.contains(origintag)) {
            if (e.getPlayer().getWorld().getEnvironment() == World.Environment.NORMAL) {
                if (e.getBed().getY() <= 99) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar("You need fresh air to sleep");
                }
            }
        }
    }

}
