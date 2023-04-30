package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.api.events.choose.contents.core.origins.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class ChoosingMenuCORE implements Listener {

    @EventHandler
    public void OnOpenNew(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
            if (e.getCurrentItem() != null) {
                //Human
                if (e.getCurrentItem() != null && !e.getCurrentItem().containsEnchantment(Enchantment.ARROW_INFINITE)) {
                    if (e.getCurrentItem().getType() == Material.PLAYER_HEAD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.human")) {
                            e.getClickedInventory().setContents(HumanContents.HumanContents());
                        }
                    }else
                    //Enderian
                    if (e.getCurrentItem().getType() == Material.ENDER_PEARL && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.enderian")) {
                            e.getClickedInventory().setContents(EnderianContents.EnderianContents());
                        }
                    }else
                    //shulk
                    if (e.getCurrentItem().getType() == Material.SHULKER_SHELL && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.shulk")) {
                            e.getClickedInventory().setContents(ShulkContents.ShulkContents());
                        }
                    }else
                    //arachnid
                    if (e.getCurrentItem().getType() == Material.COBWEB && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.arachnid")) {
                            e.getClickedInventory().setContents(ArachnidContents.ArachnidContents());
                        }
                    }else
                    //creep
                    if (e.getCurrentItem().getType() == Material.GUNPOWDER && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.creep")) {
                            e.getClickedInventory().setContents(CreepContents.CreepContents());
                        }
                    }else
                    //phantom
                    if (e.getCurrentItem().getType() == Material.PHANTOM_MEMBRANE && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.phantom")) {
                            e.getClickedInventory().setContents(PhantomContents.PhantomContents());
                        }
                    }else
                    //slimeling
                    if (e.getCurrentItem().getType() == Material.SLIME_BALL && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.slimeling")) {
                            e.getClickedInventory().setContents(SlimelingContents.SlimelingContents());
                        }
                    }else
                    //vexian
                    if (e.getCurrentItem().getType() == Material.IRON_SWORD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.vexian")) {
                            e.getClickedInventory().setContents(VexianContents.VexianContents());
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.BLAZE_POWDER && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.blazeborn")) {
                            e.getClickedInventory().setContents(BlazebornContents.BlazebornContents());
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.NETHER_STAR && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.starborne")) {
                            e.getClickedInventory().setContents(StarborneContents.StarborneContents());
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.COD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.merling")) {
                            Inventory mermaidgui = Bukkit.createInventory(p, 54, BLACK + "Merling");
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack mermaid = new ItemStack(Material.COD);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);

                            ItemStack mermaid_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack mermaid_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack mermaid_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack mermaid_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack mermaid_origin_info5 = new ItemStack(Material.FILLED_MAP);
                            ItemStack mermaid_origin_info6 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta mermaid_origin_info1_meta = mermaid_origin_info1.getItemMeta();
                            mermaid_origin_info1_meta.setDisplayName(UNDERLINE + "Gills");
                            ArrayList<String> mermaid_origin_info1_lore = new ArrayList<>();
                            mermaid_origin_info1_lore.add(WHITE + "You can ONLY breathe underwater, when raining, you can breathe on land for a short time");
                            mermaid_origin_info1_meta.setLore(mermaid_origin_info1_lore);
                            mermaid_origin_info1.setItemMeta(mermaid_origin_info1_meta);

                            ItemMeta mermaid_origin_info2_meta = mermaid_origin_info2.getItemMeta();
                            mermaid_origin_info2_meta.setDisplayName(UNDERLINE + "Wet Eyes");
                            ArrayList<String> mermaid_origin_info2_lore = new ArrayList<>();
                            mermaid_origin_info2_lore.add(WHITE + "Your vision underwater is nearly perfect");
                            mermaid_origin_info2_meta.setLore(mermaid_origin_info2_lore);
                            mermaid_origin_info2.setItemMeta(mermaid_origin_info2_meta);

                            ItemMeta mermaid_origin_info3_meta = mermaid_origin_info3.getItemMeta();
                            mermaid_origin_info3_meta.setDisplayName(UNDERLINE + "Opposing Forces");
                            ArrayList<String> mermaid_origin_info3_lore = new ArrayList<>();
                            mermaid_origin_info3_lore.add(WHITE + "You take significantly more damage from fire");
                            mermaid_origin_info3_meta.setLore(mermaid_origin_info3_lore);
                            mermaid_origin_info3.setItemMeta(mermaid_origin_info3_meta);

                            ItemMeta mermaid_origin_info4_meta = mermaid_origin_info4.getItemMeta();
                            mermaid_origin_info4_meta.setDisplayName(UNDERLINE + "Fins");
                            ArrayList<String> mermaid_origin_info4_lore = new ArrayList<>();
                            mermaid_origin_info4_lore.add(WHITE + "You can swim much faster underwater, and don't sink underwater");
                            mermaid_origin_info4_meta.setLore(mermaid_origin_info4_lore);
                            mermaid_origin_info4.setItemMeta(mermaid_origin_info4_meta);

                            ItemMeta mermaid_origin_info5_meta = mermaid_origin_info5.getItemMeta();
                            mermaid_origin_info5_meta.setDisplayName(UNDERLINE + "please don't");
                            ArrayList<String> mermaid_origin_info5_lore = new ArrayList<>();
                            mermaid_origin_info5_lore.add(WHITE + "don't eat fish, its basically cannabalism and thats gross. It gives you nausea.");
                            mermaid_origin_info5_meta.setLore(mermaid_origin_info5_lore);
                            mermaid_origin_info5.setItemMeta(mermaid_origin_info5_meta);

                            ItemMeta mermaid_origin_info6_meta = mermaid_origin_info6.getItemMeta();
                            mermaid_origin_info6_meta.setDisplayName(UNDERLINE + "Luck of the Sea");
                            ArrayList<String> mermaid_origin_info6_lore = new ArrayList<>();
                            mermaid_origin_info6_lore.add(WHITE + "You have increased fishing luck.");
                            mermaid_origin_info6_meta.setLore(mermaid_origin_info6_lore);
                            mermaid_origin_info6.setItemMeta(mermaid_origin_info6_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta mermaid_meta = mermaid.getItemMeta();
                            mermaid_meta.setDisplayName("Merling");
                            ArrayList<String> mermaid_lore = new ArrayList<>();
                            mermaid_lore.add(BLUE + "Merling Origin");
                            mermaid_meta.setLore(mermaid_lore);
                            mermaid.setItemMeta(mermaid_meta);


                            ItemStack[] mermaidgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, mermaid, air, air, air, air, air, air, mermaid_origin_info1, mermaid_origin_info2, mermaid_origin_info3, mermaid_origin_info4, mermaid_origin_info5, air, air, air, air, mermaid_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            mermaidgui.setContents(mermaidgui_items);
                            p.openInventory(mermaidgui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.AMETHYST_SHARD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.allay")) {
                            Inventory allaygui = Bukkit.createInventory(p, 54, BLACK + "Allay");

                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack allay = new ItemStack(Material.AMETHYST_SHARD);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack blank = new ItemStack(Material.PAPER);

                            ItemStack allay_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack allay_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack allay_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack allay_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack allay_origin_info5 = new ItemStack(Material.FILLED_MAP);
                            ItemStack allay_origin_info6 = new ItemStack(Material.FILLED_MAP);
                            ItemStack allay_origin_info7 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta allay_origin_info1_meta = allay_origin_info1.getItemMeta();
                            allay_origin_info1_meta.setDisplayName(UNDERLINE + "Little Fairy");
                            ArrayList<String> allay_origin_info1_lore = new ArrayList<>();
                            allay_origin_info1_lore.add(WHITE + "You have small wings, you can fly and float");
                            allay_origin_info1_meta.setLore(allay_origin_info1_lore);
                            allay_origin_info1.setItemMeta(allay_origin_info1_meta);

                            ItemMeta allay_origin_info2_meta = allay_origin_info2.getItemMeta();
                            allay_origin_info2_meta.setDisplayName(UNDERLINE + "Blue Spirit");
                            ArrayList<String> allay_origin_info2_lore = new ArrayList<>();
                            allay_origin_info2_lore.add(WHITE + "You are semi-translucent, half height, and glow in dark places. Also you're blue");
                            allay_origin_info2_meta.setLore(allay_origin_info2_lore);
                            allay_origin_info2.setItemMeta(allay_origin_info2_meta);

                            ItemMeta allay_origin_info3_meta = allay_origin_info3.getItemMeta();
                            allay_origin_info3_meta.setDisplayName(UNDERLINE + "Sounds of Music");
                            ArrayList<String> allay_origin_info3_lore = new ArrayList<>();
                            allay_origin_info3_lore.add(WHITE + "You enjoy the sounds of music, and can use a jukebox as a respawn anchor");
                            allay_origin_info3_meta.setLore(allay_origin_info3_lore);
                            allay_origin_info3.setItemMeta(allay_origin_info3_meta);

                            ItemMeta allay_origin_info4_meta = allay_origin_info4.getItemMeta();
                            allay_origin_info4_meta.setDisplayName(UNDERLINE + "COOKIES");
                            ArrayList<String> allay_origin_info4_lore = new ArrayList<>();
                            allay_origin_info4_lore.add(WHITE + "Cookies give the same saturation as steak");
                            allay_origin_info4_meta.setLore(allay_origin_info4_lore);
                            allay_origin_info4.setItemMeta(allay_origin_info4_meta);

                            ItemMeta allay_origin_info5_meta = allay_origin_info5.getItemMeta();
                            allay_origin_info5_meta.setDisplayName(UNDERLINE + "Treasure Finder");
                            ArrayList<String> allay_origin_info5_lore = new ArrayList<>();
                            allay_origin_info5_lore.add(WHITE + "You have increased chances of getting treasure loot and villagers will lower their prices for you");
                            allay_origin_info5_meta.setLore(allay_origin_info5_lore);
                            allay_origin_info5.setItemMeta(allay_origin_info5_meta);

                            ItemMeta allay_origin_info6_meta = allay_origin_info6.getItemMeta();
                            allay_origin_info6_meta.setDisplayName(UNDERLINE + "Kinda Flamable");
                            ArrayList<String> allay_origin_info6_lore = new ArrayList<>();
                            allay_origin_info6_lore.add(WHITE + "You burn easily, you take extra fire damage and have half health");
                            allay_origin_info6_meta.setLore(allay_origin_info6_lore);
                            allay_origin_info6.setItemMeta(allay_origin_info6_meta);

                            ItemMeta allay_origin_info7_meta = allay_origin_info7.getItemMeta();
                            allay_origin_info7_meta.setDisplayName(UNDERLINE + "Friendly Angel");
                            ArrayList<String> allay_origin_info7_lore = new ArrayList<>();
                            allay_origin_info7_lore.add(WHITE + "You don't like to harm animals, you get nauseous when eating meat");
                            allay_origin_info7_meta.setLore(allay_origin_info7_lore);
                            allay_origin_info7.setItemMeta(allay_origin_info7_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta allay_meta = allay.getItemMeta();
                            allay_meta.setDisplayName("Allay");
                            ArrayList<String> allay_lore = new ArrayList<>();
                            allay_lore.add(AQUA + "Allay Origin");
                            allay_meta.setLore(allay_lore);
                            allay.setItemMeta(allay_meta);


                            ItemStack[] allaygui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, allay, air, air, air, air, air, air, allay_origin_info1, allay_origin_info2, allay_origin_info3, allay_origin_info4, allay_origin_info5, air, air, air, air, allay_origin_info6, allay_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            allaygui.setContents(allaygui_items);
                            p.openInventory(allaygui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.CARROT && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.rabbit")) {
                            Inventory rabbitgui = Bukkit.createInventory(p, 54, BLACK + "Rabbit");

                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack rabbit = new ItemStack(Material.CARROT);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack rabbit_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack rabbit_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack rabbit_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack rabbit_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack rabbit_origin_info5 = new ItemStack(Material.FILLED_MAP);
                            ItemStack rabbit_origin_info6 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta rabbit_origin_info1_meta = rabbit_origin_info1.getItemMeta();
                            rabbit_origin_info1_meta.setDisplayName(UNDERLINE + "Leap");
                            ArrayList<String> rabbit_origin_info1_lore = new ArrayList<>();
                            rabbit_origin_info1_lore.add(WHITE + "You leap in the direction you're looking to");
                            rabbit_origin_info1_meta.setLore(rabbit_origin_info1_lore);
                            rabbit_origin_info1.setItemMeta(rabbit_origin_info1_meta);

                            ItemMeta rabbit_origin_info2_meta = rabbit_origin_info2.getItemMeta();
                            rabbit_origin_info2_meta.setDisplayName(UNDERLINE + "Strong Hopper");
                            ArrayList<String> rabbit_origin_info2_lore = new ArrayList<>();
                            rabbit_origin_info2_lore.add(WHITE + "You jump significantly higher");
                            rabbit_origin_info2_meta.setLore(rabbit_origin_info2_lore);
                            rabbit_origin_info2.setItemMeta(rabbit_origin_info2_meta);

                            ItemMeta rabbit_origin_info3_meta = rabbit_origin_info3.getItemMeta();
                            rabbit_origin_info3_meta.setDisplayName(UNDERLINE + "Shock Absorption");
                            ArrayList<String> rabbit_origin_info3_lore = new ArrayList<>();
                            rabbit_origin_info3_lore.add(WHITE + "You take less fall damage");
                            rabbit_origin_info3_meta.setLore(rabbit_origin_info3_lore);
                            rabbit_origin_info3.setItemMeta(rabbit_origin_info3_meta);

                            ItemMeta rabbit_origin_info4_meta = rabbit_origin_info4.getItemMeta();
                            rabbit_origin_info4_meta.setDisplayName(UNDERLINE + "Delicious");
                            ArrayList<String> rabbit_origin_info4_lore = new ArrayList<>();
                            rabbit_origin_info4_lore.add(WHITE + "You may drop a rabbit's foot when hit");
                            rabbit_origin_info4_meta.setLore(rabbit_origin_info4_lore);
                            rabbit_origin_info4.setItemMeta(rabbit_origin_info4_meta);

                            ItemMeta rabbit_origin_info5_meta = rabbit_origin_info5.getItemMeta();
                            rabbit_origin_info5_meta.setDisplayName(UNDERLINE + "Picky Eater");
                            ArrayList<String> rabbit_origin_info5_lore = new ArrayList<>();
                            rabbit_origin_info5_lore.add(WHITE + "You can only eat carrots and golden carrots");
                            rabbit_origin_info5_meta.setLore(rabbit_origin_info5_lore);
                            rabbit_origin_info5.setItemMeta(rabbit_origin_info5_meta);

                            ItemMeta rabbit_origin_info6_meta = rabbit_origin_info6.getItemMeta();
                            rabbit_origin_info6_meta.setDisplayName(UNDERLINE + "Fragile");
                            ArrayList<String> rabbit_origin_info6_lore = new ArrayList<>();
                            rabbit_origin_info6_lore.add(WHITE + "You have 3 less hearts");
                            rabbit_origin_info6_meta.setLore(rabbit_origin_info6_lore);
                            rabbit_origin_info6.setItemMeta(rabbit_origin_info6_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta rabbit_meta = rabbit.getItemMeta();
                            rabbit_meta.setDisplayName("Rabbit");
                            ArrayList<String> rabbit_lore = new ArrayList<>();
                            rabbit_lore.add(GOLD + "Bunny Origin");
                            rabbit_meta.setLore(rabbit_lore);
                            rabbit.setItemMeta(rabbit_meta);


                            ItemStack[] rabbitgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, rabbit, air, air, air, air, air, air, rabbit_origin_info1, rabbit_origin_info2, rabbit_origin_info3, rabbit_origin_info4, rabbit_origin_info5, air, air, air, air, rabbit_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            rabbitgui.setContents(rabbitgui_items);
                            p.openInventory(rabbitgui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.HONEYCOMB && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.bee")) {
                            Inventory beegui = Bukkit.createInventory(p, 54, BLACK + "Bumblebee");

                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack bee = new ItemStack(Material.HONEYCOMB);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack bee_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack bee_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack bee_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack bee_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack bee_origin_info5 = new ItemStack(Material.FILLED_MAP);
                            ItemStack bee_origin_info6 = new ItemStack(Material.FILLED_MAP);
                            ItemStack bee_origin_info7 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta bee_origin_info1_meta = bee_origin_info1.getItemMeta();
                            bee_origin_info1_meta.setDisplayName(UNDERLINE + "Featherweight");
                            ArrayList<String> bee_origin_info1_lore = new ArrayList<>();
                            bee_origin_info1_lore.add(WHITE + "You fall as gently to the ground as a feather would unless you shift");
                            bee_origin_info1_meta.setLore(bee_origin_info1_lore);
                            bee_origin_info1.setItemMeta(bee_origin_info1_meta);

                            ItemMeta bee_origin_info2_meta = bee_origin_info2.getItemMeta();
                            bee_origin_info2_meta.setDisplayName(UNDERLINE + "Poisonous");
                            ArrayList<String> bee_origin_info2_lore = new ArrayList<>();
                            bee_origin_info2_lore.add(WHITE + "Hitting someone gives them poison for 2 seconds");
                            bee_origin_info2_meta.setLore(bee_origin_info2_lore);
                            bee_origin_info2.setItemMeta(bee_origin_info2_meta);

                            ItemMeta bee_origin_info3_meta = bee_origin_info3.getItemMeta();
                            bee_origin_info3_meta.setDisplayName(UNDERLINE + "Bloom");
                            ArrayList<String> bee_origin_info3_lore = new ArrayList<>();
                            bee_origin_info3_lore.add(WHITE + "You gain regeneration when near flowers");
                            bee_origin_info3_meta.setLore(bee_origin_info3_lore);
                            bee_origin_info3.setItemMeta(bee_origin_info3_meta);

                            ItemMeta bee_origin_info4_meta = bee_origin_info4.getItemMeta();
                            bee_origin_info4_meta.setDisplayName(UNDERLINE + "Flight");
                            ArrayList<String> bee_origin_info4_lore = new ArrayList<>();
                            bee_origin_info4_lore.add(WHITE + "You can fly, just like a bee!(WHATT)");
                            bee_origin_info4_meta.setLore(bee_origin_info4_lore);
                            bee_origin_info4.setItemMeta(bee_origin_info4_meta);

                            ItemMeta bee_origin_info5_meta = bee_origin_info5.getItemMeta();
                            bee_origin_info5_meta.setDisplayName(UNDERLINE + "Nighttime");
                            ArrayList<String> bee_origin_info5_lore = new ArrayList<>();
                            bee_origin_info5_lore.add(WHITE + "You are sleepy at night, so you walk and fly slower");
                            bee_origin_info5_meta.setLore(bee_origin_info5_lore);
                            bee_origin_info5.setItemMeta(bee_origin_info5_meta);

                            ItemMeta bee_origin_info6_meta = bee_origin_info6.getItemMeta();
                            bee_origin_info6_meta.setDisplayName(UNDERLINE + "Lifespan");
                            ArrayList<String> bee_origin_info6_lore = new ArrayList<>();
                            bee_origin_info6_lore.add(WHITE + "You have 3 less hearts");
                            bee_origin_info6_meta.setLore(bee_origin_info6_lore);
                            bee_origin_info6.setItemMeta(bee_origin_info6_meta);

                            ItemMeta bee_origin_info7_meta = bee_origin_info7.getItemMeta();
                            bee_origin_info7_meta.setDisplayName(UNDERLINE + "Rain");
                            ArrayList<String> bee_origin_info7_lore = new ArrayList<>();
                            bee_origin_info7_lore.add(WHITE + "You cannot fly when in the rain and are weaker while wet");
                            bee_origin_info7_meta.setLore(bee_origin_info7_lore);
                            bee_origin_info7.setItemMeta(bee_origin_info7_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta bee_meta = bee.getItemMeta();
                            bee_meta.setDisplayName("Bumblebee");
                            ArrayList<String> bee_lore = new ArrayList<>();
                            bee_lore.add(YELLOW + "Bee Origin");
                            bee_meta.setLore(bee_lore);
                            bee.setItemMeta(bee_meta);


                            ItemStack[] beegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, bee, air, air, air, air, air, air, bee_origin_info1, bee_origin_info2, bee_origin_info3, bee_origin_info4, bee_origin_info5, air, air, air, air, bee_origin_info6, bee_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            beegui.setContents(beegui_items);
                            p.openInventory(beegui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.ELYTRA && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.elytrian")) {
                            Inventory elyrtiangui = Bukkit.createInventory(p, 54, BLACK + "Elytrian");

                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack elyrtian = new ItemStack(Material.ELYTRA);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack elyrtian_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack elyrtian_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack elyrtian_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack elyrtian_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack elyrtian_origin_info5 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta elyrtian_origin_info1_meta = elyrtian_origin_info1.getItemMeta();
                            elyrtian_origin_info1_meta.setDisplayName(UNDERLINE + "Winged");
                            ArrayList<String> elyrtian_origin_info1_lore = new ArrayList<>();
                            elyrtian_origin_info1_lore.add(WHITE + "You have Elytra wings without needing to equip any");
                            elyrtian_origin_info1_meta.setLore(elyrtian_origin_info1_lore);
                            elyrtian_origin_info1.setItemMeta(elyrtian_origin_info1_meta);

                            ItemMeta elyrtian_origin_info2_meta = elyrtian_origin_info2.getItemMeta();
                            elyrtian_origin_info2_meta.setDisplayName(UNDERLINE + "Gift of the Winds");
                            ArrayList<String> elyrtian_origin_info2_lore = new ArrayList<>();
                            elyrtian_origin_info2_lore.add(WHITE + "Every 60 seconds, you can launch yourself 20 blocks in the air");
                            elyrtian_origin_info2_meta.setLore(elyrtian_origin_info2_lore);
                            elyrtian_origin_info2.setItemMeta(elyrtian_origin_info2_meta);

                            ItemMeta elyrtian_origin_info3_meta = elyrtian_origin_info3.getItemMeta();
                            elyrtian_origin_info3_meta.setDisplayName(UNDERLINE + "Claustrophobia");
                            ArrayList<String> elyrtian_origin_info3_lore = new ArrayList<>();
                            elyrtian_origin_info3_lore.add(WHITE + "Being somewhere with a low ceiling for too long will weaken you");
                            elyrtian_origin_info3_meta.setLore(elyrtian_origin_info3_lore);
                            elyrtian_origin_info3.setItemMeta(elyrtian_origin_info3_meta);

                            ItemMeta elyrtian_origin_info4_meta = elyrtian_origin_info4.getItemMeta();
                            elyrtian_origin_info4_meta.setDisplayName(UNDERLINE + "Need for Mobility");
                            ArrayList<String> elyrtian_origin_info4_lore = new ArrayList<>();
                            elyrtian_origin_info4_lore.add(WHITE + "You cannot wear any heavy armour with prot values higher than chainmail");
                            elyrtian_origin_info4_meta.setLore(elyrtian_origin_info4_lore);
                            elyrtian_origin_info4.setItemMeta(elyrtian_origin_info4_meta);

                            ItemMeta elyrtian_origin_info5_meta = elyrtian_origin_info5.getItemMeta();
                            elyrtian_origin_info5_meta.setDisplayName(UNDERLINE + "Brittle Bones");
                            ArrayList<String> elyrtian_origin_info5_lore = new ArrayList<>();
                            elyrtian_origin_info5_lore.add(WHITE + "You take more damage from falling and flying into blocks");
                            elyrtian_origin_info5_meta.setLore(elyrtian_origin_info5_lore);
                            elyrtian_origin_info5.setItemMeta(elyrtian_origin_info5_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta elyrtian_meta = elyrtian.getItemMeta();
                            elyrtian_meta.setDisplayName("Elytrian");
                            ArrayList<String> elyrtian_lore = new ArrayList<>();
                            elyrtian_lore.add(GRAY + "Elytrian Origin");
                            elyrtian_meta.setLore(elyrtian_lore);
                            elyrtian.setItemMeta(elyrtian_meta);


                            ItemStack[] elyrtiangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, elyrtian, air, air, air, air, air, air, elyrtian_origin_info1, elyrtian_origin_info2, elyrtian_origin_info3, elyrtian_origin_info4, elyrtian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            elyrtiangui.setContents(elyrtiangui_items);
                            p.openInventory(elyrtiangui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.FEATHER && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.avian")) {
                            Inventory aviangui = Bukkit.createInventory(p, 54, BLACK + "Avian");

                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack avian = new ItemStack(Material.FEATHER);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack avian_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack avian_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack avian_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack avian_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack avian_origin_info5 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta avian_origin_info1_meta = avian_origin_info1.getItemMeta();
                            avian_origin_info1_meta.setDisplayName(UNDERLINE + "Featherweight");
                            ArrayList<String> avian_origin_info1_lore = new ArrayList<>();
                            avian_origin_info1_lore.add(WHITE + "You fall as gently to the ground as a feather would, unless you shift");
                            avian_origin_info1_meta.setLore(avian_origin_info1_lore);
                            avian_origin_info1.setItemMeta(avian_origin_info1_meta);

                            ItemMeta avian_origin_info2_meta = avian_origin_info2.getItemMeta();
                            avian_origin_info2_meta.setDisplayName(UNDERLINE + "Tailwind");
                            ArrayList<String> avian_origin_info2_lore = new ArrayList<>();
                            avian_origin_info2_lore.add(WHITE + "You are a little quicker on foot than others");
                            avian_origin_info2_meta.setLore(avian_origin_info2_lore);
                            avian_origin_info2.setItemMeta(avian_origin_info2_meta);

                            ItemMeta avian_origin_info3_meta = avian_origin_info3.getItemMeta();
                            avian_origin_info3_meta.setDisplayName(UNDERLINE + "Oviparous");
                            ArrayList<String> avian_origin_info3_lore = new ArrayList<>();
                            avian_origin_info3_lore.add(WHITE + "Whenever you wake up in the morning, you lay an egg");
                            avian_origin_info3_meta.setLore(avian_origin_info3_lore);
                            avian_origin_info3.setItemMeta(avian_origin_info3_meta);

                            ItemMeta avian_origin_info4_meta = avian_origin_info4.getItemMeta();
                            avian_origin_info4_meta.setDisplayName(UNDERLINE + "Vegetarian");
                            ArrayList<String> avian_origin_info4_lore = new ArrayList<>();
                            avian_origin_info4_lore.add(WHITE + "You can't digest any meat");
                            avian_origin_info4_meta.setLore(avian_origin_info4_lore);
                            avian_origin_info4.setItemMeta(avian_origin_info4_meta);

                            ItemMeta avian_origin_info5_meta = avian_origin_info5.getItemMeta();
                            avian_origin_info5_meta.setDisplayName(UNDERLINE + "Fresh Air");
                            ArrayList<String> avian_origin_info5_lore = new ArrayList<>();
                            avian_origin_info5_lore.add(WHITE + "When sleeping, your bed needs to be at an altitude of at least 100 blocks");
                            avian_origin_info5_meta.setLore(avian_origin_info5_lore);
                            avian_origin_info5.setItemMeta(avian_origin_info5_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta avian_meta = avian.getItemMeta();
                            avian_meta.setDisplayName("Avian");
                            ArrayList<String> avian_lore = new ArrayList<>();
                            avian_lore.add(DARK_AQUA + "Avian Origin");
                            avian_meta.setLore(avian_lore);
                            avian.setItemMeta(avian_meta);


                            ItemStack[] aviangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, avian, air, air, air, air, air, air, avian_origin_info1, avian_origin_info2, avian_origin_info3, avian_origin_info4, avian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            aviangui.setContents(aviangui_items);
                            p.openInventory(aviangui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.GOLD_INGOT && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.piglin")) {
                            Inventory piglingui = Bukkit.createInventory(p, 54, BLACK + "Piglin");
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);

                            ItemStack piglin_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack piglin_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack piglin_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack piglin_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack piglin_origin_info5 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta piglin_origin_info1_meta = piglin_origin_info1.getItemMeta();
                            piglin_origin_info1_meta.setDisplayName(UNDERLINE + "I like to be SHINY");
                            ArrayList<String> piglin_origin_info1_lore = new ArrayList<>();
                            piglin_origin_info1_lore.add(WHITE + "Golden tools deal extra damage and gold armour has more protection");
                            piglin_origin_info1_meta.setLore(piglin_origin_info1_lore);
                            piglin_origin_info1.setItemMeta(piglin_origin_info1_meta);

                            ItemMeta piglin_origin_info2_meta = piglin_origin_info2.getItemMeta();
                            piglin_origin_info2_meta.setDisplayName(UNDERLINE + "Friendly Frenemies");
                            ArrayList<String> piglin_origin_info2_lore = new ArrayList<>();
                            piglin_origin_info2_lore.add(WHITE + "Piglins won't attack you unless provoked, Brutes will still attack on sight");
                            piglin_origin_info2_meta.setLore(piglin_origin_info2_lore);
                            piglin_origin_info2.setItemMeta(piglin_origin_info2_meta);

                            ItemMeta piglin_origin_info3_meta = piglin_origin_info3.getItemMeta();
                            piglin_origin_info3_meta.setDisplayName(UNDERLINE + "Nether Dweller");
                            ArrayList<String> piglin_origin_info3_lore = new ArrayList<>();
                            piglin_origin_info3_lore.add(WHITE + "Your natural spawn is in the Nether and you can only eat meat");
                            piglin_origin_info3_meta.setLore(piglin_origin_info3_lore);
                            piglin_origin_info3.setItemMeta(piglin_origin_info3_meta);

                            ItemMeta piglin_origin_info4_meta = piglin_origin_info4.getItemMeta();
                            piglin_origin_info4_meta.setDisplayName(UNDERLINE + "Colder Realms");
                            ArrayList<String> piglin_origin_info4_lore = new ArrayList<>();
                            piglin_origin_info4_lore.add(WHITE + "When outside of the Nether, you zombify and become immune to fire and slower");
                            piglin_origin_info4_meta.setLore(piglin_origin_info4_lore);
                            piglin_origin_info4.setItemMeta(piglin_origin_info4_meta);

                            ItemMeta piglin_origin_info5_meta = piglin_origin_info5.getItemMeta();
                            piglin_origin_info5_meta.setDisplayName(UNDERLINE + "BLUE FIRE SPOOKY");
                            ArrayList<String> piglin_origin_info5_lore = new ArrayList<>();
                            piglin_origin_info5_lore.add(WHITE + "You are afraid of soul fire, becoming weak when near it");
                            piglin_origin_info5_meta.setLore(piglin_origin_info5_lore);
                            piglin_origin_info5.setItemMeta(piglin_origin_info5_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta piglin_meta = piglin.getItemMeta();
                            piglin_meta.setDisplayName("Piglin");
                            ArrayList<String> piglin_lore = new ArrayList<>();
                            piglin_lore.add(GOLD + "Piglin Origin");
                            piglin_meta.setLore(piglin_lore);
                            piglin.setItemMeta(piglin_meta);


                            ItemStack[] piglingui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, piglin, air, air, air, air, air, air, piglin_origin_info1, piglin_origin_info2, piglin_origin_info3, piglin_origin_info4, piglin_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            piglingui.setContents(piglingui_items);
                            p.openInventory(piglingui);
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.DRAGON_BREATH && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.dragonborne")) {
                            Inventory dragongui = Bukkit.createInventory(p, 54, BLACK + "Dragonborne");
                            ItemStack disconnect = new ItemStack(Material.REDSTONE_BLOCK);

                            ItemMeta disconnectmeta = disconnect.getItemMeta();
                            disconnectmeta.setDisplayName(RED + "Disconnect");
                            disconnectmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            disconnectmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            ArrayList<String> disconnectlore = new ArrayList<>();
                            disconnectlore.add(RED + "Disconnect player from choosing");
                            disconnectmeta.setLore(disconnectlore);
                            disconnect.setItemMeta(disconnectmeta);
                            ItemStack close = new ItemStack(Material.BARRIER);
                            ItemStack next = new ItemStack(Material.ARROW);
                            ItemStack dragon = new ItemStack(Material.DRAGON_BREATH);
                            ItemStack air = new ItemStack(Material.AIR);
                            ItemStack blank = new ItemStack(Material.PAPER);

                            ItemStack dragon_origin_info1 = new ItemStack(Material.FILLED_MAP);
                            ItemStack dragon_origin_info2 = new ItemStack(Material.FILLED_MAP);
                            ItemStack dragon_origin_info3 = new ItemStack(Material.FILLED_MAP);
                            ItemStack dragon_origin_info4 = new ItemStack(Material.FILLED_MAP);
                            ItemStack dragon_origin_info5 = new ItemStack(Material.FILLED_MAP);
                            ItemStack dragon_origin_info6 = new ItemStack(Material.FILLED_MAP);


                            ItemMeta dragon_origin_info1_meta = dragon_origin_info1.getItemMeta();
                            dragon_origin_info1_meta.setDisplayName(UNDERLINE + "Mighty Wings");
                            ArrayList<String> dragon_origin_info1_lore = new ArrayList<>();
                            dragon_origin_info1_lore.add(WHITE + "You spawn with a permanent Elytra");
                            dragon_origin_info1_meta.setLore(dragon_origin_info1_lore);
                            dragon_origin_info1.setItemMeta(dragon_origin_info1_meta);

                            ItemMeta dragon_origin_info2_meta = dragon_origin_info2.getItemMeta();
                            dragon_origin_info2_meta.setDisplayName(UNDERLINE + "Heart of a Dragon");
                            ArrayList<String> dragon_origin_info2_lore = new ArrayList<>();
                            dragon_origin_info2_lore.add(WHITE + "You have 6 more hearts and can only eat meat");
                            dragon_origin_info2_meta.setLore(dragon_origin_info2_lore);
                            dragon_origin_info2.setItemMeta(dragon_origin_info2_meta);

                            ItemMeta dragon_origin_info3_meta = dragon_origin_info3.getItemMeta();
                            dragon_origin_info3_meta.setDisplayName(UNDERLINE + "Breath of Fire");
                            ArrayList<String> dragon_origin_info3_lore = new ArrayList<>();
                            dragon_origin_info3_lore.add(WHITE + "You can shoot a dragon fireball upon shift-clicking with a 30 second cooldown");
                            dragon_origin_info3_meta.setLore(dragon_origin_info3_lore);
                            dragon_origin_info3.setItemMeta(dragon_origin_info3_meta);

                            ItemMeta dragon_origin_info4_meta = dragon_origin_info4.getItemMeta();
                            dragon_origin_info4_meta.setDisplayName(UNDERLINE + "Sharp Tips");
                            ArrayList<String> dragon_origin_info4_lore = new ArrayList<>();
                            dragon_origin_info4_lore.add(WHITE + "You take more damage from arrows.");
                            dragon_origin_info4_meta.setLore(dragon_origin_info4_lore);
                            dragon_origin_info4.setItemMeta(dragon_origin_info4_meta);

                            ItemMeta dragon_origin_info5_meta = dragon_origin_info5.getItemMeta();
                            dragon_origin_info5_meta.setDisplayName(UNDERLINE + "Resistance");
                            ArrayList<String> dragon_origin_info5_lore = new ArrayList<>();
                            dragon_origin_info5_lore.add(WHITE + "You take no knockback and have extremely tough and protective skin");
                            dragon_origin_info5_meta.setLore(dragon_origin_info5_lore);
                            dragon_origin_info5.setItemMeta(dragon_origin_info5_meta);

                            ItemMeta dragon_origin_info6_meta = dragon_origin_info6.getItemMeta();
                            dragon_origin_info6_meta.setDisplayName(UNDERLINE + "Hot Touch");
                            ArrayList<String> dragon_origin_info6_lore = new ArrayList<>();
                            dragon_origin_info6_lore.add(WHITE + "You can light furnaces with your dragon fireball");
                            dragon_origin_info6_meta.setLore(dragon_origin_info6_lore);
                            dragon_origin_info6.setItemMeta(dragon_origin_info6_meta);

                            ItemMeta close_meta = close.getItemMeta();
                            close_meta.setDisplayName(RED + "Close");
                            ArrayList<String> close_lore = new ArrayList<>();
                            close_lore.add(RED + "Cancel Choosing");
                            close_meta.setLore(close_lore);
                            close.setItemMeta(close_meta);

                            ItemMeta next_meta = next.getItemMeta();
                            next_meta.setDisplayName(BLUE + "MENU");
                            ArrayList<String> next_lore = new ArrayList<>();
                            next_lore.add(WHITE + "All Origins");
                            next_meta.setLore(next_lore);
                            next.setItemMeta(next_meta);

                            ItemMeta dragon_meta = dragon.getItemMeta();
                            dragon_meta.setDisplayName("Dragonborne");
                            ArrayList<String> dragon_lore = new ArrayList<>();
                            dragon_lore.add(DARK_PURPLE + "Dragon Origin");
                            dragon_meta.setLore(dragon_lore);
                            dragon.setItemMeta(dragon_meta);


                            ItemStack[] dragongui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, dragon, air, air, air, air, air, air, dragon_origin_info1, dragon_origin_info2, dragon_origin_info3, dragon_origin_info4, dragon_origin_info5, air, air, air, air, dragon_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, disconnect};
                            dragongui.setContents(dragongui_items);
                            p.openInventory(dragongui);
                        }
                    }
                }
            }

        }
    }
}
