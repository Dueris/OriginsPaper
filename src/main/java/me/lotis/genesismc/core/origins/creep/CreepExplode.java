package me.lotis.genesismc.core.origins.creep;

import me.lotis.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
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
import java.util.Random;
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
        if (originid == 2356555) {
            cooldown.remove(p.getUniqueId());
            new BukkitRunnable() {
                Material block = e.getPlayer().getLocation().getBlock().getType();

                @Override
                public void run() {

                    if (p.isSneaking()) {
                        if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 3500)) {
                            if(p.isSneaking()){
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                explodecooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            }
                            if (originid == 2356555) {
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
                            }



                        }

                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                        if(!cooldown.containsKey(p.getUniqueId()) || (System.currentTimeMillis() - cooldown.get(p.getUniqueId()) >= 3400)){
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
                                p.teleportAsync(p.getLocation());
                                p.damage(3);
                                e.setCancelled(true);
                                this.cancel();
                            }

                        }else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2800)) {
                            p.sendActionBar(ChatColor.RED + "--");
                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2100)) {
                            p.sendActionBar(ChatColor.YELLOW + "----");
                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 1400)) {
                            p.sendActionBar(ChatColor.GREEN + "------");
                        } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) <= 700)) {
                            p.sendActionBar(ChatColor.BLUE + "--------");
                        }



                    }
                }

            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 5L);
        } else {
            //do nothing
        }
    }


}
