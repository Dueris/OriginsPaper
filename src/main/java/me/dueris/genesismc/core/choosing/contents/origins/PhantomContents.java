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

public class PhantomContents {
    public static @Nullable ItemStack @NotNull [] PhantomContents(){

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
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
        ItemStack phantom_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack phantom_origin_info6 = new ItemStack(Material.FILLED_MAP);


        ItemMeta phantom_origin_info1_meta = phantom_origin_info1.getItemMeta();
        phantom_origin_info1_meta.setDisplayName(UNDERLINE + "Translucent");
        ArrayList<String> phantom_origin_info1_lore = new ArrayList<>();
        phantom_origin_info1_lore.add(WHITE + "You are slightly translucent, and at night you become more solid");
        phantom_origin_info1_meta.setLore(phantom_origin_info1_lore);
        phantom_origin_info1.setItemMeta(phantom_origin_info1_meta);

        ItemMeta phantom_origin_info2_meta = phantom_origin_info2.getItemMeta();
        phantom_origin_info2_meta.setDisplayName(UNDERLINE + "Not Really a Vampire");
        ArrayList<String> phantom_origin_info2_lore = new ArrayList<>();
        phantom_origin_info2_lore.add(WHITE + "You take damage from sunlight");
        phantom_origin_info2_meta.setLore(phantom_origin_info2_lore);
        phantom_origin_info2.setItemMeta(phantom_origin_info2_meta);

        ItemMeta phantom_origin_info3_meta = phantom_origin_info3.getItemMeta();
        phantom_origin_info3_meta.setDisplayName(UNDERLINE + "Phasing" + DARK_GRAY + "Press F to activate, or use item");
        ArrayList<String> phantom_origin_info3_lore = new ArrayList<>();
        phantom_origin_info3_lore.add(WHITE + "You can turn into your \"Phantom Form\", allowing you to walk through walls");
        phantom_origin_info3_meta.setLore(phantom_origin_info3_lore);
        phantom_origin_info3.setItemMeta(phantom_origin_info3_meta);

        ItemMeta phantom_origin_info4_meta = phantom_origin_info4.getItemMeta();
        phantom_origin_info4_meta.setDisplayName(UNDERLINE + "Fast Metabolism");
        ArrayList<String> phantom_origin_info4_lore = new ArrayList<>();
        phantom_origin_info4_lore.add(WHITE + "While in Phantom Form, you loose twice as much hunger");
        phantom_origin_info4_meta.setLore(phantom_origin_info4_lore);
        phantom_origin_info4.setItemMeta(phantom_origin_info4_meta);

        ItemMeta phantom_origin_info5_meta = phantom_origin_info5.getItemMeta();
        phantom_origin_info5_meta.setDisplayName(UNDERLINE + "Fragile Creature");
        ArrayList<String> phantom_origin_info5_lore = new ArrayList<>();
        phantom_origin_info5_lore.add(WHITE + "You have 3 less hearts");
        phantom_origin_info5_meta.setLore(phantom_origin_info5_lore);
        phantom_origin_info5.setItemMeta(phantom_origin_info5_meta);

        ItemMeta phantom_origin_info6_meta = phantom_origin_info6.getItemMeta();
        phantom_origin_info6_meta.setDisplayName(UNDERLINE + "Invisibility");
        ArrayList<String> phantom_origin_info6_lore = new ArrayList<>();
        phantom_origin_info6_lore.add(WHITE + "While phantomized, you become fully invisible.");
        phantom_origin_info6_meta.setLore(phantom_origin_info6_lore);
        phantom_origin_info6.setItemMeta(phantom_origin_info6_meta);

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

        ItemMeta phantom_meta = phantom.getItemMeta();
        phantom_meta.setDisplayName("Phantom");
        ArrayList<String> phantom_lore = new ArrayList<>();
        phantom_lore.add(BLUE + "Phantom Origin");
        phantom_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        phantom_meta.setLore(phantom_lore);
        phantom.setItemMeta(phantom_meta);


        ItemStack[] phantomgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, phantom, air, air, air, air, air, air, phantom_origin_info1, phantom_origin_info2, phantom_origin_info3, phantom_origin_info4, phantom_origin_info5, air, air, air, air, phantom_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return phantomgui_items;
    }
}
