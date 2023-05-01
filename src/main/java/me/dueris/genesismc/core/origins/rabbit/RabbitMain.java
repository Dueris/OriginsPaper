package me.dueris.genesismc.core.origins.rabbit;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RabbitMain implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!(e.getDamager() instanceof Player)) return;

        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-rabbit")) {
            Random random = new Random();
            int randInt = random.nextInt(9);
            p.sendMessage(String.valueOf(randInt));
            if (randInt == 0) {
                p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.RABBIT_FOOT));
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-rabbit")) {
            @NotNull ItemStack item = e.getItem();

            if (item == null) return;;
            if (!item.getType().isEdible()) return;

            if (!(item.getType() == Material.CARROT || item.getType() == Material.GOLDEN_CARROT)) {
                e.setCancelled(true);
            }

        }
    }
}
