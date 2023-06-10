package me.dueris.genesismc.core.factory.powers.food;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.core.factory.powers.Powers.carrot_only;

public class CarrotOnly implements Listener {
    @EventHandler
    public void onItemConsume(PlayerInteractEvent e) {
        if (carrot_only.contains(e.getPlayer())) {
            @NotNull ItemStack item = e.getItem();

            if (item == null) return;
            if (!item.getType().isEdible()) return;

            if (!(item.getType() == Material.CARROT || item.getType() == Material.GOLDEN_CARROT)) {
                e.setCancelled(true);
            }

        }
    }
}
