package me.dueris.genesismc.core.origins.creep;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreepExplode implements Listener {
    private final HashMap<UUID, Long> cooldown;
    private final HashMap<UUID, Long> explodecooldown;

    public CreepExplode() {
        this.explodecooldown = new HashMap<>();
        this.cooldown = new HashMap<>();
    }

    @EventHandler
    public void onShiftCreep(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        PersistentDataContainer datacreep = p.getPersistentDataContainer();
        int creepcanid = datacreep.get(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER);
        if (originid == 2356555) {
            if(creepcanid == 2){
            cooldown.remove(p.getUniqueId());
            new BukkitRunnable() {
                Material block = e.getPlayer().getLocation().getBlock().getType();

                @Override
                public void run() {

                    if (p.isSneaking()) {
                        if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 3300)) {
                            if (p.isSneaking()) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            } else {
                                this.cancel();
                            }

                        }

                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                        if (!cooldown.containsKey(p.getUniqueId()) || (System.currentTimeMillis() - cooldown.get(p.getUniqueId()) >= 2900)) {

                            List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                            for (Entity tmp : nearby)
                                if (tmp instanceof Damageable && tmp != p)
                                    ((Damageable) tmp).damage(15);
                            List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                            for (Entity tmp2 : nearby2)
                                if (tmp2 instanceof Damageable && tmp2 != p)
                                    ((Damageable) tmp2).damage(10);
                            List<Entity> nearby3 = p.getNearbyEntities(5, 5, 5);
                            for (Entity tmp3 : nearby3)
                                if (tmp3 instanceof Damageable && tmp3 != p)
                                    ((Damageable) tmp3).damage(5);
                            e.setCancelled(true);
                            cancel();

                            if (p.getWorld().isThundering()) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 1, true, false, false));
                                p.getWorld().createExplosion(p.getLocation(), 6);
                                p.teleportAsync(p.getLocation());
                                p.damage(3);
                                e.setCancelled(true);
                                this.cancel();
                            } else {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 1, true, false, false));
                                p.getWorld().createExplosion(p.getLocation(), 3);
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                p.teleportAsync(p.getLocation());
                                p.damage(3);
                                e.setCancelled(true);
                                this.cancel();
                            }

                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2300)) {
                            p.sendActionBar(ChatColor.RED + "--");
                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 1700)) {
                            p.sendActionBar(ChatColor.YELLOW + "----");
                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 1100)) {
                            p.sendActionBar(ChatColor.GREEN + "------");
                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) <= 500)) {
                            p.sendActionBar(ChatColor.BLUE + "--------");
                        }


                    }else{
                        this.cancel();
                    }
                }

            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 5L);
        } else {
            }

        } else {
        }
    }


}
