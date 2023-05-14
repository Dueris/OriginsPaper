package me.dueris.genesismc.core.origins;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static org.bukkit.Material.SHULKER_SHELL;

public class OriginHandler implements Listener {
    @EventHandler
    public void onhitShulk(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
            PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SHULKER_HURT, 10.0F, 5.0F);
            } else if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_HURT, 10.0F, 5.0F);
            }
        }
    }

    @EventHandler
    public void onDeathShulk(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
            PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SHULKER_DEATH, 10.0F, 5.0F);
                Random random = new Random();
                int r = random.nextInt(100);
                if (r <= 8) {
                    e.getEntity().getLocation().getWorld().dropItem(e.getEntity().getLocation(), new ItemStack(SHULKER_SHELL, 1));
                }
            } else if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10.0F, 5.0F);
            }
        }
    }

    @EventHandler
    public void onTargetShulk(EntityTargetEvent e){
        if(e.getEntity() instanceof ShulkerBullet){
            PersistentDataContainer data = e.getTarget().getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
                if(e.getTarget() instanceof Player){
                    e.setCancelled(true);
                }
            }
        }
    }
}
