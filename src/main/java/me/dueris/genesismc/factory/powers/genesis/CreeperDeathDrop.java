package me.dueris.genesismc.factory.powers.genesis;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.dueris.genesismc.factory.powers.Power.creeper_head_death_drop;

public class CreeperDeathDrop implements Listener {

    @EventHandler
    public void onCreepDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (creeper_head_death_drop.contains(e.getPlayer())) {
            if (e.getEntity().getType() == EntityType.CREEPER) {
                Creeper killer = (Creeper) e.getEntity();
                if (killer.isPowered()) {
                    e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                }
            } else if (e.getEntity().getType() == EntityType.PLAYER) {
                Player killerp = e.getEntity();
                PersistentDataContainer datak = killerp.getPersistentDataContainer();
                if (creeper_head_death_drop.contains(e.getPlayer())) {
                    if (p.getWorld().isThundering() && e.getEntity().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)) {
                        PersistentDataContainer edata = e.getEntity().getPersistentDataContainer();
                        if (creeper_head_death_drop.contains(p)) {
                            e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                        }


                    }
                }

            }
        }
    }

}
