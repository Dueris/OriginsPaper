package me.dueris.genesismc.core.factory.powers.item;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

import static me.dueris.genesismc.core.factory.powers.Powers.launch_into_air;
import static org.bukkit.ChatColor.GRAY;

public class LaunchAir implements Listener {

    public static HashMap<UUID, Long> cooldownAfterElytrian = new HashMap<>();

    @EventHandler
    public static void RespawnLaunchItem(PlayerRespawnEvent e) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);
        if (launch_into_air.contains(OriginPlayer.getOrigin(e.getPlayer()).getTag())) {
            e.getPlayer().getInventory().addItem(launchitem);
        }
    }

    @EventHandler
    public static void DeathLaunchItem(PlayerDeathEvent e) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);
        if (!e.getDrops().contains(launchitem)) return;
        if (launch_into_air.contains(OriginPlayer.getOrigin(e.getPlayer()).getTag())) {
            e.getDrops().remove(launchitem);
        }
    }

    @EventHandler
    public void ExecuteLaunch(PlayerInteractEvent e) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);
        if (launch_into_air.contains(OriginPlayer.getOrigin(e.getPlayer()).getTag())) {
            if (e.getItem() == null) return;
            if (e.getItem().equals(launchitem)) {
                e.getPlayer().setVelocity(new Vector(0, 2, 0));
                e.setCancelled(true);
            }
        }
    }

}
