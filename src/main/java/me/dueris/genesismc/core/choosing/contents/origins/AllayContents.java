package me.dueris.genesismc.core.choosing.contents.origins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class AllayContents {
    public static @Nullable ItemStack @NotNull [] AllayContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
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

        ItemMeta menumeta = menu.getItemMeta();
        menumeta.setDisplayName(ChatColor.AQUA + "Return");
        menumeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> menulore = new ArrayList<>();
        menumeta.setLore(menulore);
        menu.setItemMeta(menumeta);

        ItemMeta allay_meta = allay.getItemMeta();
        allay_meta.setDisplayName("Allay");
        ArrayList<String> allay_lore = new ArrayList<>();
        allay_lore.add(AQUA + "Allay Origin");
        allay_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        allay_meta.setLore(allay_lore);
        allay.setItemMeta(allay_meta);


        ItemStack[] allaygui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, allay, air, air, air, air, air, air, allay_origin_info1, allay_origin_info2, allay_origin_info3, allay_origin_info4, allay_origin_info5, air, air, air, air, allay_origin_info6, allay_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return allaygui_items;
    }
}
