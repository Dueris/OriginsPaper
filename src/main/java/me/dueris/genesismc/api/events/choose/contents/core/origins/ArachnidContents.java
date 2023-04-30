package me.dueris.genesismc.api.events.choose.contents.core.origins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class ArachnidContents {
    public static @Nullable ItemStack @NotNull [] ArachnidContents(){
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
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack spider = new ItemStack(Material.COBWEB);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack spider_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack spider_origin_info5 = new ItemStack(Material.FILLED_MAP);


        ItemMeta spider_origin_info1_meta = spider_origin_info1.getItemMeta();
        spider_origin_info1_meta.setDisplayName(UNDERLINE + "SpiderMan");
        ArrayList<String> spider_origin_info1_lore = new ArrayList<>();
        spider_origin_info1_lore.add(WHITE + "You can climb up walls, but not when in the rain");
        spider_origin_info1_meta.setLore(spider_origin_info1_lore);
        spider_origin_info1.setItemMeta(spider_origin_info1_meta);

        ItemMeta spider_origin_info2_meta = spider_origin_info2.getItemMeta();
        spider_origin_info2_meta.setDisplayName(UNDERLINE + "Weaver");
        spider_origin_info2_meta.setLore(Arrays.asList(WHITE + "You hinder your foes with", WHITE + "cobwebs upon attacking them"));
        spider_origin_info2.setItemMeta(spider_origin_info2_meta);

        ItemMeta spider_origin_info3_meta = spider_origin_info3.getItemMeta();
        spider_origin_info3_meta.setDisplayName(UNDERLINE + "Squishable");
        ArrayList<String> spider_origin_info3_lore = new ArrayList<>();
        spider_origin_info3_lore.add(WHITE + "You have 3 less hearts");
        spider_origin_info3_meta.setLore(spider_origin_info3_lore);
        spider_origin_info3.setItemMeta(spider_origin_info3_meta);

        ItemMeta spider_origin_info4_meta = spider_origin_info4.getItemMeta();
        spider_origin_info4_meta.setDisplayName(UNDERLINE + "Tiny Carnivore");
        ArrayList<String> spider_origin_info4_lore = new ArrayList<>();
        spider_origin_info4_lore.add(WHITE + "You can only eat meat");
        spider_origin_info4_meta.setLore(spider_origin_info4_lore);
        spider_origin_info4.setItemMeta(spider_origin_info4_meta);

        ItemMeta close_meta = close.getItemMeta();
        close_meta.setDisplayName(RED + "Close");
        ArrayList<String> close_lore = new ArrayList<>();
        close_lore.add(RED + "Cancel Choosing");
        close_meta.setLore(close_lore);
        close.setItemMeta(close_meta);

        ItemMeta menumeta = menu.getItemMeta();
        menumeta.setDisplayName(ChatColor.AQUA + "Return");
        menumeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> menulore = new ArrayList<>();
        menumeta.setLore(menulore);
        menu.setItemMeta(menumeta);

        ItemMeta spider_meta = spider.getItemMeta();
        spider_meta.setDisplayName("Arachnid");
        ArrayList<String> spider_lore = new ArrayList<>();
        spider_lore.add(RED + "Spider Origin");
        spider_meta.setLore(spider_lore);
        spider.setItemMeta(spider_meta);


        ItemStack[] spidergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, spider, air, air, air, air, air, air, spider_origin_info1, spider_origin_info2, spider_origin_info3, spider_origin_info4, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return spidergui_items;
    }
}
