package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.arthropod;

public class Arthropod implements Listener {

    @EventHandler
    public void OnAttack(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (arthropod.contains(p.getUniqueId().toString())) {
                if (e.getDamager() != null) {
                    Entity damager = e.getDamager();
                    if (damager.getType() == EntityType.PLAYER) {
                        Player d = (Player) damager;
                        if (d.getInventory().getItemInMainHand() != null && d.getInventory().getItemInMainHand().getItemMeta() != null) {
                            if (d.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.DAMAGE_ARTHROPODS)) {
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

}
