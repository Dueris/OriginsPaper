package me.dueris.genesismc.core.origins.piglin;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PiglinMain implements Listener {


    ArrayList<Integer> piglinsHit;

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player)) return;
        Player p = (Player) e.getTarget();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-phantom")) {
            if (e.getEntity().getType() == EntityType.PIGLIN) {
                if (!piglinsHit.contains(e.getEntity().getEntityId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        Player p = (Player) e.getDamager();
        LivingEntity entity = (LivingEntity) e.getEntity();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-phantom")) {
            if (e.getEntity().getType() == EntityType.PIGLIN) {
                if (piglinsHit.contains(e.getEntity().getEntityId())) return;
                if (entity.getHealth() - e.getFinalDamage() <= 0) return;
                piglinsHit.add(e.getEntity().getEntityId());
            }
        }
    }
}
