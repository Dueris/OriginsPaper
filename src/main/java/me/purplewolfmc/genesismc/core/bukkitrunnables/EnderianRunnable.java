package me.purplewolfmc.genesismc.core.bukkitrunnables;

import me.purplewolfmc.genesismc.core.origins.enderian.EnderWater;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

import static org.bukkit.Material.ENDER_PEARL;

public class EnderianRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.getScoreboardTags().contains("enderian")) {
                Block b = p.getWorld().getHighestBlockAt(p.getLocation());

                ItemStack infinpearl = new ItemStack(ENDER_PEARL);

                ItemMeta pearl_meta = infinpearl.getItemMeta();
                pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                ArrayList<String> pearl_lore = new ArrayList<>();
                pearl_meta.setUnbreakable(true);
                pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                pearl_meta.setLore(pearl_lore);
                infinpearl.setItemMeta(pearl_meta);
                if(p.getScoreboardTags().contains("enderian")){
                    p.setCooldown(ENDER_PEARL, 1);
                }

                if (!p.getActivePotionEffects().equals(PotionEffectType.INVISIBILITY)) {
                    if(!p.getGameMode().equals(GameMode.SPECTATOR)) {
                        p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 3);
                    }else{

                    }
                } else {
                }

                Random random = new Random();

                int r = random.nextInt(3000);
                if (r == (int) 3 || r == (int) 9 || r == (int) 11 || r == (int) 998 || r == (int) 2279 || r == (int) 989) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 10, 9);
                }
                p.setMaximumAir(20);
                p.setHealthScale(24);
                if(p.getInventory().getItemInMainHand().equals(infinpearl)){
                    if (p.getInventory().getItemInMainHand().getAmount() == 2) {
                            int amt = p.getInventory().getItemInMainHand().getAmount();
                            p.getInventory().getItemInMainHand().setAmount(amt - 1);
                    }
                }
                if(p.getInventory().getItemInMainHand().getAmount() != 1 && p.getInventory().getItemInMainHand().getAmount() != 0){
                    int amt = p.getInventory().getItemInMainHand().getAmount();
                    if(p.getEquipment().getItemInMainHand().equals(infinpearl)) {
                        p.getInventory().getItemInMainHand().setAmount(amt - 1);
                    }
                }
                if (p.getScoreboardTags().contains("enderian")) {
                    if(p.getNearbyEntities(15, 15, 15).equals(EntityType.SPLASH_POTION)){
                        p.damage(2);
                    }
                }
            }

        }
        Bukkit.getOnlinePlayers().stream().forEach(p -> {

        });
    }

    }

