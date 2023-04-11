package me.dueris.genesismc.core.bukkitrunnables;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreepRunnable extends BukkitRunnable {

    private final HashMap<UUID, Long> cooldown;

    public CreepRunnable() {
        this.cooldown = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 2356555) {

                    if(p.getWorld().isThundering()){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 2, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 2, true, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 1, true, false, false));

                }

                List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                for (Entity tmp : nearby2)
                    if (tmp instanceof Cat)
                        ((Damageable) p).damage(2);
            }else{
                p.removeScoreboardTag("can-explode");
            }
        }
    }
}
