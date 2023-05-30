package me.dueris.genesismc.core.origins;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OriginHandler extends BukkitRunnable implements Listener {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (OriginPlayer.getOriginTag(p).contains("genesis:origin-piglin")) {
                if (p.getWorld().getEnvironment() != World.Environment.NETHER) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10, 0, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 0, false, false, false));
                }
            }
        }
    }


    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
            Player p = (Player) e.getEntity();
            if (OriginPlayer.getOriginTag(p).equalsIgnoreCase("genesis:origin-enderian")) {
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10, 1F);
            }
        }
    }

    @EventHandler
    public void onTargetShulk(EntityTargetEvent e) {
        if (e.getEntity() instanceof ShulkerBullet) {
            if (e.getTarget() instanceof Player p) {
                if (OriginPlayer.getOriginTag(p).equalsIgnoreCase("genesis:origin-shulk")) {
                    if (e.getTarget() instanceof Player) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}
