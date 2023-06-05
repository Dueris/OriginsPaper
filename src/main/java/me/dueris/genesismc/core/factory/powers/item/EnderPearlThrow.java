package me.dueris.genesismc.core.factory.powers.item;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.powers.Powers.throw_ender_pearl;
import static org.bukkit.Material.ENDER_PEARL;

public class EnderPearlThrow implements Listener {

    @EventHandler
    public void teleportDamgeOff(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (throw_ender_pearl.contains(e.getPlayer()) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            p.teleport(e.getTo());
        }

    }

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

        Player p = e.getPlayer();
        if (throw_ender_pearl.contains(e.getPlayer())) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() != null) {
                    if (e.getItem().equals(infinpearl)) {
                        if (p.getCooldown(ENDER_PEARL) == 0 && p.getGameMode() != GameMode.CREATIVE) {
                            p.getInventory().addItem(infinpearl);
                            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                                if (p.getInventory().getItemInMainHand().isSimilar(infinpearl)) ;
                                p.getInventory().getItemInMainHand().setAmount(1);
                            }, 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void RespawnPearl(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (throw_ender_pearl.contains(e.getPlayer())) {
            if (!p.getInventory().contains(infinpearl) && p.getGameMode() != GameMode.CREATIVE) {
                p.getInventory().addItem(infinpearl);
            }
        }
    }

    @EventHandler
    public void CancelDrop(PlayerDropItemEvent e) {
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);

        if (throw_ender_pearl.contains(e.getPlayer()))
            if (e.getItemDrop().getItemStack().isSimilar(infinpearl)) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void RemovePearl(PlayerDeathEvent e) {
        Player p = e.getEntity();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);
        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (throw_ender_pearl.contains(e.getPlayer())) {
            e.getDrops().remove(infinpearl);
            e.getDrops().add(new ItemStack(ENDER_PEARL));
        }

    }
}
