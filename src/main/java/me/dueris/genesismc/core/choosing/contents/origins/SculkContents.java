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

public class SculkContents {
    public static @Nullable ItemStack @NotNull [] SculkContents(){
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
        ItemStack sculk = new ItemStack(Material.ECHO_SHARD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack sculk_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info7 = new ItemStack(Material.FILLED_MAP);
        ItemStack sculk_origin_info8 = new ItemStack(Material.FILLED_MAP);


        ItemMeta sculk_origin_info1_meta = sculk_origin_info1.getItemMeta();
        sculk_origin_info1_meta.setDisplayName(UNDERLINE + "One of Them");
        ArrayList<String> sculk_origin_info1_lore = new ArrayList<>();
        sculk_origin_info1_lore.add(WHITE + "You spawn in the Deep Dark");
        sculk_origin_info1_meta.setLore(sculk_origin_info1_lore);
        sculk_origin_info1.setItemMeta(sculk_origin_info1_meta);

        ItemMeta sculk_origin_info2_meta = sculk_origin_info2.getItemMeta();
        sculk_origin_info2_meta.setDisplayName(UNDERLINE + "Amongst Your People");
        ArrayList<String> sculk_origin_info2_lore = new ArrayList<>();
        sculk_origin_info2_lore.add(WHITE + "You get buffs while near sculk blocks");
        sculk_origin_info2_meta.setLore(sculk_origin_info2_lore);
        sculk_origin_info2.setItemMeta(sculk_origin_info2_meta);

        ItemMeta sculk_origin_info3_meta = sculk_origin_info3.getItemMeta();
        sculk_origin_info3_meta.setDisplayName(UNDERLINE + "Best Friends Forever");
        ArrayList<String> sculk_origin_info3_lore = new ArrayList<>();
        sculk_origin_info3_lore.add(WHITE + "The Warden wont attack you, and you dont trigger Sculk Shriekers");
        sculk_origin_info3_meta.setLore(sculk_origin_info3_lore);
        sculk_origin_info3.setItemMeta(sculk_origin_info3_meta);

        ItemMeta sculk_origin_info4_meta = sculk_origin_info4.getItemMeta();
        sculk_origin_info4_meta.setDisplayName(UNDERLINE + "Afraid of the Light");
        ArrayList<String> sculk_origin_info4_lore = new ArrayList<>();
        sculk_origin_info4_lore.add(WHITE + "You are weaker while in sunlight");
        sculk_origin_info4_meta.setLore(sculk_origin_info4_lore);
        sculk_origin_info4.setItemMeta(sculk_origin_info4_meta);

        ItemMeta sculk_origin_info5_meta = sculk_origin_info5.getItemMeta();
        sculk_origin_info5_meta.setDisplayName(UNDERLINE + "It Grows");
        ArrayList<String> sculk_origin_info5_lore = new ArrayList<>();
        sculk_origin_info5_lore.add(WHITE + "Upon dying, a small patch of sculk will grow around you, you gain some saturation");
        sculk_origin_info5_meta.setLore(sculk_origin_info5_lore);
        sculk_origin_info5.setItemMeta(sculk_origin_info5_meta);

        ItemMeta sculk_origin_info6_meta = sculk_origin_info6.getItemMeta();
        sculk_origin_info6_meta.setDisplayName(UNDERLINE + "Echo Pulse");
        ArrayList<String> sculk_origin_info6_lore = new ArrayList<>();
        sculk_origin_info6_lore.add(WHITE + "You can see all entities around you, you gain some saturation");
        sculk_origin_info6_meta.setLore(sculk_origin_info6_lore);
        sculk_origin_info6.setItemMeta(sculk_origin_info6_meta);

        ItemMeta sculk_origin_info7_meta = sculk_origin_info7.getItemMeta();
        sculk_origin_info7_meta.setDisplayName(UNDERLINE + "Decaying Essence");
        ArrayList<String> sculk_origin_info7_lore = new ArrayList<>();
        sculk_origin_info7_lore.add(WHITE + "All armour you wear will slowly deteriorate");
        sculk_origin_info7_meta.setLore(sculk_origin_info7_lore);
        sculk_origin_info7.setItemMeta(sculk_origin_info7_meta);

        ItemMeta sculk_origin_info8_meta = sculk_origin_info8.getItemMeta();
        sculk_origin_info8_meta.setDisplayName(UNDERLINE + "Carrier of Echos");
        ArrayList<String> sculk_origin_info8_lore = new ArrayList<>();
        sculk_origin_info8_lore.add(WHITE + "You emmit a sonic boom upon Shift-Clicking your Boom keybind, or your Boom item(30 second cooldown)");
        sculk_origin_info8_meta.setLore(sculk_origin_info8_lore);
        sculk_origin_info8.setItemMeta(sculk_origin_info8_meta);

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

        ItemMeta sculk_meta = sculk.getItemMeta();
        sculk_meta.setDisplayName("Sculkling");
        ArrayList<String> sculk_lore = new ArrayList<>();
        sculk_lore.add(BLUE + "Sculk Origin");
        sculk_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        sculk_meta.setLore(sculk_lore);
        sculk.setItemMeta(sculk_meta);


        ItemStack[] sculkgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, sculk, air, air, air, air, air, air, sculk_origin_info1, sculk_origin_info2, sculk_origin_info3, sculk_origin_info4, sculk_origin_info5, air, air, air, air, sculk_origin_info6, sculk_origin_info7, sculk_origin_info8, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return sculkgui_items;
    }
}
