package me.dueris.genesismc.core.origins.creep;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class CreepMain implements Listener {


    @EventHandler
    public void onLook(EntityTargetEvent e) {
        //EntityType en = e.getEntityType(); not needed
        if (e.getEntity() instanceof Creeper && (e.getTarget() instanceof Player)) {

            Player p = (Player) e.getTarget();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 2356555) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onCreepDeath(PlayerDeathEvent e) {
        Player p = (Player) e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 2356555) {
            Random random = new Random();
                if(e.getEntity().getType() == EntityType.CREEPER){
                    Creeper killer = (Creeper) e.getEntity();
                    if(killer.isPowered()){
                        e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                    }
                }else if(e.getEntity().getType() == EntityType.PLAYER){
                    Player killerp = e.getEntity();
                    PersistentDataContainer datak = killerp.getPersistentDataContainer();
                    int originidk = datak.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                    if (originid == 2356555) {
                        if (p.getWorld().isThundering() && e.getEntity().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)) {
                            PersistentDataContainer edata = e.getEntity().getPersistentDataContainer();
                            int originide = edata.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                            if(originide == 2356555){
                                e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                            }


                        }
                    }

                }
            }
    }

    @EventHandler
    public void onCreepDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 2356555) {
                if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
                    e.setDamage(e.getFinalDamage() - 7);
                }else{
                    if(e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                        e.setDamage(e.getFinalDamage() + 2);
                    }else{e.setDamage(e.getFinalDamage() + 4);}
                }
            }
        }
    }

    @EventHandler
    public void onUseBow(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 2356555) {
            if (e.getItem() != null) {
                if (e.getItem().getType().equals(Material.BOW)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    }
