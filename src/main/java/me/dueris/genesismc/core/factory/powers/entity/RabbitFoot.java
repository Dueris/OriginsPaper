package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.rabbit_drop_foot;

public class RabbitFoot implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!(e.getDamager() instanceof Player)) return;

        if (rabbit_drop_foot.contains(p)) {
            Random random = new Random();
            int randInt = random.nextInt(9);
            if (randInt == 4) {
                p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.RABBIT_FOOT));
            }
        }
    }
}
