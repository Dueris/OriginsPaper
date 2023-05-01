package me.dueris.genesismc.core.origins.enderian;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class EnderMain implements Listener {
    private final HashMap<UUID, Long> cooldown;
    public EnderMain() {
        this.cooldown = new HashMap<>();
    }




    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (originid == 0401065) {

            Random random = new Random();
            int r = random.nextInt(3000);
            if (r == 3 || r == 9 || r == 11 || r == 998 || r == 2279 || r == 989) {
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 10, 9);
            }
        }
    }

    @EventHandler
    public void onEvent1(PlayerJoinEvent e) {
        Player p = (Player) e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        if (p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)) {
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (originid == 0401065) {
                if (!p.getActivePotionEffects().equals(PotionEffectType.INVISIBILITY)) {
                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 3);
                }
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 9);
                p.setHealthScale(24);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Endermite){
            Player p = (Player) e.getEntity();
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (originid == 0401065) {
                p.damage(2);
                e.getDamager().setGlowing(true);
            }
        }
    }
    @EventHandler
    public void onDeathWater(PlayerDeathEvent e){
        Player p = (Player) e.getEntity();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (originid == 0401065) {
            Random random = new Random();
            int r = random.nextInt(2);
            if (p.isInWaterOrRainOrBubbleColumn()) {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10, 5);
                e.setDeathMessage(p.getName() + " melted to death");
            }
                    p.getLocation().getWorld().dropItem(p.getLocation(), new ItemStack(Material.ENDER_PEARL, r));
            }
        if (originid == 0401065) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10, 5);
        }
        }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e){
        Player p = (Player) e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (originid == 0401065) {
            if(e.getItem().getType().equals(Material.PUMPKIN_PIE)){
                p.getWorld().createExplosion(p.getLocation(), 0);
                p.setHealth(1);
            }
            if(e.getItem().getType().equals(Material.POTION)){
                p.damage(2);
            }

        }
    }


}

