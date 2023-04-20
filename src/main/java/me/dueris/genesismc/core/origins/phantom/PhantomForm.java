package me.dueris.genesismc.core.origins.phantom;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Material.*;

public class PhantomForm implements Listener {

    @EventHandler
    public void onKey(PlayerInteractEvent e) {
        ItemStack spectatorswitch = new ItemStack(Material.FEATHER);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phantom Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.setLore(pearl_lore);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spectatorswitch.setItemMeta(switch_meta);
        Action action = e.getAction();

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        if (originid == 7300041) {

            if (e.getItem() != null) {
                if (e.getItem().isSimilar(spectatorswitch)) {
                    if (phantomid == 1) {
                        if(p.getGameMode() != GameMode.SPECTATOR) {

                            if(p.getFoodLevel() > 6){
                                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 2);
                                p.sendActionBar(DARK_AQUA + "Activated Phantom Form");
                                p.setInvisible(true);
                                p.setSilent(true);
                                p.setCollidable(false);
                                p.setInvulnerable(false);
                                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.8);

                            }else{
                                p.sendMessage(RED + "You must be able to sprint to switch forms");
                            }

                            CraftPlayer craftPlayer = (CraftPlayer) p;

                        }else{p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");}
                    } else if (phantomid == 2) {
                        if(p.getGameMode() != GameMode.SPECTATOR) {

                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                            p.sendActionBar(DARK_AQUA + "Deactivated Phantom Form");
                            p.setInvisible(false);
                            p.setSilent(false);
                            p.setCollidable(true);
                            p.setInvulnerable(false);
                            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);

                        }else{p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");}
                    } else {
                        p.sendMessage(RED + "Error: Switching could not be executed");
                    }
                    e.setCancelled(true);
                }
            }

        }

    }

        //replaced move handler with phantom form runnable


    @EventHandler
    public void CancelSpectate(PlayerStartSpectatingEntityEvent e){
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        if (originid == 7300041) {
            if(phantomid == 2) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        ItemStack spectatorswitch = new ItemStack(Material.FEATHER);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phantom Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.setLore(pearl_lore);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spectatorswitch.setItemMeta(switch_meta);

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 7300041) {
            if (e.getItemDrop().getItemStack().isSimilar(spectatorswitch)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        ItemStack spectatorswitch = new ItemStack(Material.FEATHER);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phantom Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        switch_meta.setLore(pearl_lore);
        spectatorswitch.setItemMeta(switch_meta);

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (e.getDrops().contains(spectatorswitch)) {
                e.getDrops().remove(spectatorswitch);
            }
    }
}
