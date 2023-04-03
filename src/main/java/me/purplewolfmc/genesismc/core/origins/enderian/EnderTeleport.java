package me.purplewolfmc.genesismc.core.origins.enderian;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.ENDER_PEARL;

public class EnderTeleport implements Listener {

    @EventHandler
    public void teleportDamgeOff(PlayerTeleportEvent e) {
        Player p = (Player) e.getPlayer();

        if (p.getScoreboardTags().contains("enderian")) {
            if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                e.setCancelled(true);
                p.setNoDamageTicks(1);
                p.teleport(e.getTo());
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 9);
            }
        }else{
    }
    }

    public static HashMap<UUID, Integer> thrownPearls = new HashMap<UUID, Integer>();

    @EventHandler
    public void onThrow(final PlayerInteractEvent e) {
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);

        Player p = (Player) e.getPlayer();
        if (p.getScoreboardTags().contains("enderian")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() != null) {
                    if (e.getItem().equals(infinpearl)) {
                        p.getInventory().addItem(infinpearl);
                    } else if (e.getItem().equals(null)) {
                        //no
                    }
                }
            } else if (e.getItem() != null) {
                if (e.getItem().equals(infinpearl)) {
                    p.getInventory().removeItem(infinpearl);
                } else if (e.getItem().equals(null)) {
                }
            }
        }else{
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() != null) {
                    if (e.getItem().equals(infinpearl)) {
                        e.setCancelled(true);
                        e.getItem().setType(AIR);
                    }
                }
                    }

        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = (Player) e.getPlayer();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (p.getScoreboardTags().contains("enderian")) {
            if (!p.getInventory().contains(infinpearl)) {
                p.getInventory().addItem(infinpearl);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EnderPearl)) {
            return;
        } else {
            ((Player) e.getEntity().getShooter()).setCooldown(Material.ENDER_PEARL, 1);
        }
    }

    @EventHandler
    public void onDeathEnder(PlayerDeathEvent e) {
        Player p = (Player) e.getEntity();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (p.getScoreboardTags().contains("enderian")) {
            e.getDrops().remove(infinpearl);
            e.getDrops().add(new ItemStack(ENDER_PEARL));
        }
    }

}