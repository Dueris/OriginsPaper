package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.rabbit_drop_foot;

public class RabbitFoot implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!(e.getDamager() instanceof Player)) return;

        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (rabbit_drop_foot.contains(origintag)) {
            Random random = new Random();
            int randInt = random.nextInt(9);
            p.sendMessage(String.valueOf(randInt));
            if (randInt == 4) {
                p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.RABBIT_FOOT));
            }
        }
    }
}
