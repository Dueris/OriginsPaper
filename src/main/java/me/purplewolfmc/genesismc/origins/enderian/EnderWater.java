package me.purplewolfmc.genesismc.origins.enderian;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class EnderWater implements Listener {
    public EnderWater() {
    }
    public static int baseTemperature = 0;



    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getScoreboardTags().contains("enderian")) {
            if(e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){

            }else{
                Player p = (Player)e.getEntity();
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 10.0F, 5.0F);
            }

        }
        if(e.getEntity().getType().equals(EntityType.PLAYER)){
            Player p = (Player) e.getEntity();
            if(p.getScoreboardTags().contains("enderian")){
                if(e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
                    int dmg = (int) e.getDamage();
                    e.setDamage(0);
                    e.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e){
            Player p = e.getPlayer();
            if(p.getScoreboardTags().contains("enderian")){
                if(e.getItem().equals(Material.POTION)){
                    p.damage(2);
                }
            }

    }

}
