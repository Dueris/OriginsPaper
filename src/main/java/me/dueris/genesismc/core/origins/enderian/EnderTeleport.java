package me.dueris.genesismc.core.origins.enderian;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.bukkit.Material.ENDER_PEARL;

public class EnderTeleport implements Listener {

    @EventHandler

    public void teleportDamgeOff(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);

        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
            if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                e.setCancelled(true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15, 2, false, false, false));
                p.setNoDamageTicks(1);
                p.teleport(e.getTo());
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 9);
            }
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
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() != null) {
                    if (e.getItem().equals(infinpearl)) {
                        if(p.getCooldown(ENDER_PEARL) == 0){
                            if(p.getInventory().getItemInOffHand().equals(infinpearl)){

                                p.getInventory().getItemInOffHand().add(1);
                            }else{
                                p.getInventory().addItem(infinpearl);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
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
            Player p = (Player) e.getEntity().getShooter();
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if(origintag.equalsIgnoreCase("genesis:origin-enderian"));
            ((Player) e.getEntity().getShooter()).setCooldown(Material.ENDER_PEARL, 45);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(origintag.equalsIgnoreCase("genesis:origin-enderian"))
            if (e.getItemDrop().getItemStack().isSimilar(infinpearl)) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onDeathEnder(PlayerDeathEvent e) {
        Player p = e.getEntity();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
                e.getDrops().remove(infinpearl);
                e.getDrops().add(new ItemStack(ENDER_PEARL));
        }

    }
}
