package me.purplewolfmc.genesismc.core.origins.enderian;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

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
        Player p = (Player) e.getPlayer();
        if (p.getScoreboardTags().contains("enderian")) {

            Random random = new Random();

            int r = random.nextInt(3000);
            if (r == (int) 3 || r == (int) 9 || r == (int) 11 || r == (int) 998 || r == (int) 2279 || r == (int) 989) {
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 10, 9);
            }
            p.setHealthScale(24);
        }
    }

    @EventHandler
    public void onEvent1(PlayerJoinEvent e) {
        Player p = (Player) e.getPlayer();
        if (p.getScoreboardTags().contains("enderian")) {
            if (!p.getActivePotionEffects().equals(PotionEffectType.INVISIBILITY)) {
                p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 3);
            } else {
                //do nothing
            }
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 9);
            p.setHealthScale(24);
        }
    }

    @EventHandler
    public void onDeathWater(PlayerDeathEvent e){
        Player p = (Player) e.getEntity();
        if (p.getScoreboardTags().contains("enderian")) {
            Random random = new Random();
            int r = random.nextInt(2);
            if (p.isInWaterOrRainOrBubbleColumn()) {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10, 5);
                e.setDeathMessage(p.getName() + " melted to death");

            }
                    p.getLocation().getWorld().dropItem(p.getLocation(), new ItemStack(Material.ENDER_PEARL, r));
            }
        if (p.getScoreboardTags().contains("enderian")) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10, 5);
        }
        }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e){
        Player p = (Player) e.getPlayer();
        if (p.getScoreboardTags().contains("enderian")) {
            if(e.getItem().getType().equals(Material.PUMPKIN_PIE)){
                p.damage(16);
            }
        }
    }


}

