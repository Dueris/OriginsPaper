package me.dueris.genesismc.core.items;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.Material.ENDER_PEARL;


public class Items extends BukkitRunnable implements Listener {

    Particle.DustTransition whitedust = new Particle.DustTransition(Color.fromRGB(139, 243, 252), Color.fromRGB(224, 253, 255), 0.7F);

    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {

            if (p.getInventory().getItemInMainHand().equals(orb)) {
                Location rightHand = getRightSide(p.getEyeLocation(), 0.45).subtract(0, .6, 0);
                p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, rightHand, 1, 0.25F, -0.5, 0.25F, whitedust);
            }
            if (p.getInventory().getItemInOffHand().equals(orb)) {
                Location leftHand = getLeftSide(p.getEyeLocation(), 0.45).subtract(0, .6, 0);
                p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, leftHand, 1, 0.25F, -0.5, 0.25F, whitedust);
            }
        }
    }

    @EventHandler
    public void OnCraftAttempt(PrepareItemCraftEvent e) {
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);

        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phantom Form");
        ArrayList<String> phanlore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        switch_meta.setLore(phanlore);
        spectatorswitch.setItemMeta(switch_meta);

        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);

        for (ItemStack ingredient : e.getInventory().getMatrix()) {
            if ((ingredient != null && ingredient.isSimilar(infinpearl)) || (ingredient != null && ingredient.isSimilar(spectatorswitch)) || (ingredient != null && ingredient.isSimilar(launchitem))) {
                e.getInventory().setResult(null); // Set the crafting result to null
                return;
            }
        }
    }

}
