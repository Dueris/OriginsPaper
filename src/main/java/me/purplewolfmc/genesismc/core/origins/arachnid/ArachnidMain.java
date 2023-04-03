package me.purplewolfmc.genesismc.core.origins.arachnid;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;
import java.util.Random;

import static org.bukkit.Material.*;

public class ArachnidMain implements Listener {
    public static EnumSet<Material> meat;
    public static EnumSet<Material> excludable;
    static {
        meat = EnumSet.of(COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON, COOKED_COD, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON, BEEF, CHICKEN, MUTTON, COD, PORKCHOP, RABBIT, SALMON);
        excludable = EnumSet.of(GOLDEN_APPLE, POTION, SPLASH_POTION, LINGERING_POTION, ENCHANTED_GOLDEN_APPLE, SUSPICIOUS_STEW, CHORUS_FRUIT);
    }
    @EventHandler
    public void onEatArachnid(PlayerInteractEvent e){
        if(e.getItem() != null) {
                if (!meat.contains(e.getItem().getType()) && excludable.contains(e.getItem())) {
                    e.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (p.getScoreboardTags().contains("arachnid")) {
                Location loc = e.getEntity().getLocation();
                Block b = loc.getBlock();
                Random random = new Random();
                int r = random.nextInt(5);
                if (r == 3) {
                    b.setType(COBWEB);
                }
            }
        }
    }

    @EventHandler
    public void onDamagePoison(EntityDamageEvent e){
        if(e.getCause().equals(EntityDamageEvent.DamageCause.POISON)){
            if(e.getEntity() instanceof Player){
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
    }

}
