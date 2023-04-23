package me.dueris.genesismc.core.origins.arachnid;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.*;

public class ArachnidMain implements Listener {
    public static EnumSet<Material> meat;
    public static EnumSet<Material> excludable;
    static {
        meat = EnumSet.of(COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON, COOKED_COD, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON,
                BEEF, CHICKEN, MUTTON, COD, PORKCHOP, RABBIT, SALMON, TROPICAL_FISH, PUFFERFISH, RABBIT_STEW);
        excludable = EnumSet.of(GOLDEN_APPLE, POTION, SPLASH_POTION, LINGERING_POTION, ENCHANTED_GOLDEN_APPLE, SUSPICIOUS_STEW, CHORUS_FRUIT);
    }

    @EventHandler
    public void onEatArachnid(PlayerInteractEvent e){
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        Player p = e.getPlayer();
        if (originid == 1709012) {
            if(e.getItem() != null){
            if(!meat.contains(e.getItem().getType()) && !excludable.contains(e.getItem().getType()) && e.getItem().getType().isEdible()) {
                if (e.getAction().isRightClick()) {
                    e.setCancelled(true);
                }
            }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 1709012) {
                Location loc = e.getEntity().getLocation();
                Block b = loc.getBlock();
                Random random = new Random();
                int r = random.nextInt(10);
                if (r == 3) {
                    b.setType(COBWEB);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if(b.getType() == AIR || b.getType() == COBWEB){
                                b.setType(AIR);
                                this.cancel();
                            }else{
                                this.cancel();
                            }
                        }

                    }.runTaskTimer(GenesisMC.getPlugin(), 40L, 5L);
                }
            }
        }
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 1709012) {
                if(e.getDamager() != null){
                    Entity damager = e.getDamager();
                    if(damager.getType() == EntityType.PLAYER){
                        Player d = (Player) damager;
                        if (d.getInventory().getItemInMainHand() != null && d.getInventory().getItemInMainHand().getItemMeta() != null) {
                        if(d.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
                            if (d.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) == 1) {
                                p.damage(1);
                            } else if (d.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) == 2) {
                                p.damage(2);
                            } else if (d.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) == 3) {
                                p.damage(3);
                            } else if (d.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) == 4) {
                                p.damage(4);
                            } else if (d.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) == 5) {
                                p.damage(5);
                            }
                        }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamagePoison(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (e.getCause().equals(EntityDamageEvent.DamageCause.POISON)) {
                if (originid == 1709012) {
                    if (e.getEntity() instanceof Player) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                }
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                if (originid == 1709012) {
                    if (e.getEntity() instanceof Player) {
                        e.setDamage(e.getDamage() + 2);
                    }
                }
            }
        }
    }

}
