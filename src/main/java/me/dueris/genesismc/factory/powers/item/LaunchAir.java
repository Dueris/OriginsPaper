package me.dueris.genesismc.factory.powers.item;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.dueris.genesismc.factory.powers.Power.launch_into_air;
import static org.bukkit.ChatColor.GRAY;

public class LaunchAir implements Listener {

    @EventHandler
    public static void RespawnLaunchItem(PlayerRespawnEvent e) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);
        if (launch_into_air.contains(e.getPlayer())) {
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
        if (launch_into_air.contains(e.getPlayer())) {
            e.getDrops().remove(launchitem);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);

        if (e.getItemDrop().getItemStack().isSimilar(launchitem)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTransfer(InventoryClickEvent e) {
        if (e.getClick().isKeyboardClick()) {
            if (e.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;
            if (e.getView().getBottomInventory().getItem(e.getHotbarButton()) != null) {
                ItemStack transferred = e.getView().getBottomInventory().getItem(e.getHotbarButton());
                if (transferred == null) return;
                if (transferred.getType().equals(Material.FEATHER)) {
                    ItemStack launchitem = new ItemStack(Material.FEATHER);
                    ItemMeta launchmeta = launchitem.getItemMeta();
                    launchmeta.setDisplayName(GRAY + "Launch");
                    launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    launchitem.setItemMeta(launchmeta);

                    if (transferred.isSimilar(launchitem)) {
                        e.setCancelled(true);
                    }
                }
            }

            return;
        }
        if (e.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
            if (e.getView().getTopInventory().getHolder() != null && e.getView().getTopInventory().getHolder().equals(e.getWhoClicked()))
                return;
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.FEATHER)) {
                ItemStack launchitem = new ItemStack(Material.FEATHER);
                ItemMeta launchmeta = launchitem.getItemMeta();
                launchmeta.setDisplayName(GRAY + "Launch");
                launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                launchitem.setItemMeta(launchmeta);

                if (e.getCurrentItem().isSimilar(launchitem)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    public static final HashMap<UUID, Integer> cooldownBeforeElytrian = new HashMap<>();
    public static final HashMap<UUID, Long> cooldownAfterElytrian = new HashMap<>();
    public static final ArrayList<UUID> canLaunch = new ArrayList<>();

    public static void doLaunch(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownAfterElytrian.containsKey(p.getUniqueId())) {
                    if (System.currentTimeMillis() - cooldownAfterElytrian.get(p.getUniqueId()) >= 0) {
                        p.sendActionBar(ChatColor.RED + "|||||||||");
                    }
                    if (System.currentTimeMillis() - cooldownAfterElytrian.get(p.getUniqueId()) >= 75000) {
                        p.sendActionBar(ChatColor.RED + "|||||||");
                    }
                    if (System.currentTimeMillis() - cooldownAfterElytrian.get(p.getUniqueId()) >= 15000) {
                        p.sendActionBar(ChatColor.YELLOW + "|||||");
                    }
                    if (System.currentTimeMillis() - cooldownAfterElytrian.get(p.getUniqueId()) >= 22500) {
                        p.sendActionBar(ChatColor.YELLOW + "|||");
                    }
                    if (System.currentTimeMillis() - cooldownAfterElytrian.get(p.getUniqueId()) >= 30000) {
                        cooldownAfterElytrian.remove(p.getUniqueId());
                        p.sendActionBar(ChatColor.GREEN + "-");
                        canLaunch.add(p.getUniqueId());
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0L, 10L);
    }

    @EventHandler
    public void ExecuteLaunch(PlayerInteractEvent e) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);
        Player p = e.getPlayer();
        if (launch_into_air.contains(e.getPlayer())) {
            if (e.getItem() == null) return;
            if (p.isSneaking()) return;
            if (cooldownAfterElytrian.containsKey(p.getUniqueId())) return;
            if (e.getItem().equals(launchitem)) {
//

                cooldownBeforeElytrian.put(p.getUniqueId(), 0);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldownBeforeElytrian.replace(p.getUniqueId(), cooldownBeforeElytrian.get(p.getUniqueId()) + 1);
                        cooldownAfterElytrian.put(p.getUniqueId(), System.currentTimeMillis());
                        canLaunch.remove(p);
                        doLaunch(p);
                        p.setVelocity(new Vector(p.getVelocity().getX(), p.getVelocity().getY() + 1.7, p.getVelocity().getZ()));
                        this.cancel();
                    }
                }.runTaskTimer(GenesisMC.getPlugin(), 0L, 2L);
                e.setCancelled(true);
            }
        }
    }

}
