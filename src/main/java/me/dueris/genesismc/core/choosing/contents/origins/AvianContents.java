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

public class AvianContents {
    public static @Nullable ItemStack @NotNull [] AvianContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
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

        ItemMeta menumeta = menu.getItemMeta();
        menumeta.setDisplayName(ChatColor.AQUA + "Return");
        menumeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> menulore = new ArrayList<>();
        menumeta.setLore(menulore);
        menu.setItemMeta(menumeta);

        ItemMeta avian_meta = avian.getItemMeta();
        avian_meta.setDisplayName("Avian");
        ArrayList<String> avian_lore = new ArrayList<>();
        avian_lore.add(DARK_AQUA + "Avian Origin");
        avian_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        avian_meta.setLore(avian_lore);
        avian.setItemMeta(avian_meta);


        ItemStack[] aviangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, avian, air, air, air, air, air, air, avian_origin_info1, avian_origin_info2, avian_origin_info3, avian_origin_info4, avian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return aviangui_items;
    }
}
