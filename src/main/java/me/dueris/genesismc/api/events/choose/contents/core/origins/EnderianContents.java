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

public class EnderianContents {
    public static @Nullable ItemStack @NotNull [] EnderianContents(){
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
        ItemStack ender = new ItemStack(Material.ENDER_PEARL);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack ender_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack ender_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info5 = new ItemStack(Material.FILLED_MAP);


        ItemMeta ender_origin_info1_meta = ender_origin_info1.getItemMeta();
        ender_origin_info1_meta.setDisplayName(UNDERLINE + "Teleportaion");
        ender_origin_info1_meta.setLore(Arrays.asList(WHITE + "You have an infinite ender pearl", WHITE + "that deals no damage"));
        ender_origin_info1.setItemMeta(ender_origin_info1_meta);

        ItemMeta ender_origin_info2_meta = ender_origin_info2.getItemMeta();
        ender_origin_info2_meta.setDisplayName(UNDERLINE + "Hydrophobia");
        ender_origin_info2_meta.setLore(Arrays.asList(WHITE + "You take damage while in", WHITE + "contact with water"));
        ender_origin_info2.setItemMeta(ender_origin_info2_meta);

        ItemMeta ender_origin_info3_meta = ender_origin_info3.getItemMeta();
        ender_origin_info3_meta.setDisplayName(UNDERLINE + "Delicate touch");
        ArrayList<String> ender_origin_info3_lore = new ArrayList<>();
        ender_origin_info3_lore.add(WHITE + "You have silk touch hands");
        ender_origin_info3_meta.setLore(ender_origin_info3_lore);
        ender_origin_info3.setItemMeta(ender_origin_info3_meta);

        ItemMeta ender_origin_info4_meta = ender_origin_info4.getItemMeta();
        ender_origin_info4_meta.setDisplayName(UNDERLINE + "Brethren of the End");
        ender_origin_info4_meta.setLore(Arrays.asList(WHITE + "Enderman don't get mad at you", WHITE + "upon looking at them"));
        ender_origin_info4.setItemMeta(ender_origin_info4_meta);

        ItemMeta ender_origin_info5_meta = ender_origin_info5.getItemMeta();
        ender_origin_info5_meta.setDisplayName(UNDERLINE + "Bearer of Pearls");
        ArrayList<String> ender_origin_info5_lore = new ArrayList<>();
        ender_origin_info5_lore.add(WHITE + "You always drop 0-2 ender pearls upon death");
        ender_origin_info5_meta.setLore(ender_origin_info5_lore);
        ender_origin_info5.setItemMeta(ender_origin_info5_meta);

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

        ItemMeta ender_meta = ender.getItemMeta();
        ender_meta.setDisplayName("Enderian");
        ArrayList<String> ender_lore = new ArrayList<>();
        ender_lore.add(LIGHT_PURPLE + "Enderman Origin");
        ender_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        ender_meta.setLore(ender_lore);
        ender.setItemMeta(ender_meta);


        ItemStack[] endergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, ender, air, air, air, air, air, air, ender_origin_info1, ender_origin_info2, ender_origin_info3, ender_origin_info4, ender_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};
        return endergui_items;
    }
}
