package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.lay_eggs;
import static org.bukkit.Material.EGG;

public class LayEggs implements Listener {

    @EventHandler
    public void LayEgg(PlayerBedLeaveEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (lay_eggs.contains(origintag)) {
            Player p = e.getPlayer();
            long time = Bukkit.getServer().getWorld(p.getWorld().getName()).getTime();

            if (time == 0) {
                p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(EGG));

            }
        }
    }

}
