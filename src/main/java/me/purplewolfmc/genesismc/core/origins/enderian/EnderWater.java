package me.purplewolfmc.genesismc.core.origins.enderian;

import me.purplewolfmc.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EnderWater implements Listener {
    public EnderWater() {
    }
    public static int baseTemperature = 0;



    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
            Player p = (Player) e.getEntity();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 0401065) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {

                } else {

                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10.0F, 5.0F);
                }

            }
            if (e.getEntity().getType().equals(EntityType.PLAYER)) {
                if (originid == 0401065) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                        int dmg = (int) e.getDamage();
                        e.setDamage(0);
                        e.setCancelled(true);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e){
            Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 0401065) {
                if(e.getItem().equals(Material.POTION)){
                    p.damage(2);
                }
            }

    }

}
