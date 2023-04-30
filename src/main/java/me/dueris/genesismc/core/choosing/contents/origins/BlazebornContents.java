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

public class BlazebornContents {
    public static @Nullable ItemStack @NotNull [] BlazebornContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack blaze = new ItemStack(Material.BLAZE_POWDER);
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
        ItemStack blaze_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack blaze_origin_info7 = new ItemStack(Material.FILLED_MAP);


        ItemMeta blaze_origin_info1_meta = blaze_origin_info1.getItemMeta();
        blaze_origin_info1_meta.setDisplayName(UNDERLINE + "Born from Flames");
        ArrayList<String> blaze_origin_info1_lore = new ArrayList<>();
        blaze_origin_info1_lore.add(WHITE + "Your natural spawn is in the Nether");
        blaze_origin_info1_meta.setLore(blaze_origin_info1_lore);
        blaze_origin_info1.setItemMeta(blaze_origin_info1_meta);

        ItemMeta blaze_origin_info2_meta = blaze_origin_info2.getItemMeta();
        blaze_origin_info2_meta.setDisplayName(UNDERLINE + "Burning Wrath");
        ArrayList<String> blaze_origin_info2_lore = new ArrayList<>();
        blaze_origin_info2_lore.add(WHITE + "When on fire, you deal additional damage");
        blaze_origin_info2_meta.setLore(blaze_origin_info2_lore);
        blaze_origin_info2.setItemMeta(blaze_origin_info2_meta);

        ItemMeta blaze_origin_info3_meta = blaze_origin_info3.getItemMeta();
        blaze_origin_info3_meta.setDisplayName(UNDERLINE + "Fire Immunity");
        ArrayList<String> blaze_origin_info3_lore = new ArrayList<>();
        blaze_origin_info3_lore.add(WHITE + "You are immune to all types of fire damage");
        blaze_origin_info3_meta.setLore(blaze_origin_info3_lore);
        blaze_origin_info3.setItemMeta(blaze_origin_info3_meta);

        ItemMeta blaze_origin_info4_meta = blaze_origin_info4.getItemMeta();
        blaze_origin_info4_meta.setDisplayName(UNDERLINE + "To Hot for.. Uh.. Ya.. Water?");
        ArrayList<String> blaze_origin_info4_lore = new ArrayList<>();
        blaze_origin_info4_lore.add(WHITE + "You damage while in water, and Merlings deal more damage to you");
        blaze_origin_info4_meta.setLore(blaze_origin_info4_lore);
        blaze_origin_info4.setItemMeta(blaze_origin_info4_meta);

        ItemMeta blaze_origin_info5_meta = blaze_origin_info5.getItemMeta();
        blaze_origin_info5_meta.setDisplayName(UNDERLINE + "Hotblooded");
        ArrayList<String> blaze_origin_info5_lore = new ArrayList<>();
        blaze_origin_info5_lore.add(WHITE + "Due to your hot body, venom burns up, making you immune to poison");
        blaze_origin_info5_meta.setLore(blaze_origin_info5_lore);
        blaze_origin_info5.setItemMeta(blaze_origin_info5_meta);

        ItemMeta blaze_origin_info6_meta = blaze_origin_info6.getItemMeta();
        blaze_origin_info6_meta.setDisplayName(UNDERLINE + "Opposite Forces");
        ArrayList<String> blaze_origin_info6_lore = new ArrayList<>();
        blaze_origin_info6_lore.add(WHITE + "You are much weaker in colder biomes and at high altitudes");
        blaze_origin_info6_meta.setLore(blaze_origin_info6_lore);
        blaze_origin_info6.setItemMeta(blaze_origin_info6_meta);

        ItemMeta blaze_origin_info7_meta = blaze_origin_info7.getItemMeta();
        blaze_origin_info7_meta.setDisplayName(UNDERLINE + "Flames of the Nether");
        ArrayList<String> blaze_origin_info7_lore = new ArrayList<>();
        blaze_origin_info7_lore.add(WHITE + "Upon hitting someone, they are set on fire");
        blaze_origin_info7_meta.setLore(blaze_origin_info7_lore);
        blaze_origin_info7.setItemMeta(blaze_origin_info7_meta);

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

        ItemMeta blaze_meta = blaze.getItemMeta();
        blaze_meta.setDisplayName("Blazeborn");
        ArrayList<String> blaze_lore = new ArrayList<>();
        blaze_lore.add(GOLD + "Blaze Origin");
        blaze_meta.setLore(blaze_lore);
        blaze_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        blaze.setItemMeta(blaze_meta);


        ItemStack[] blazegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, blaze, air, air, air, air, air, air, blaze_origin_info1, blaze_origin_info2, blaze_origin_info3, blaze_origin_info4, blaze_origin_info5, air, air, air, air, blaze_origin_info6, blaze_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, disconnect};

        return blazegui_items;
    }
}
