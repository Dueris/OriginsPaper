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

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class VexianContents {
    public static @Nullable ItemStack @NotNull [] VexianContents(){
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
        ItemStack vex = new ItemStack(Material.IRON_SWORD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack vex_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack vex_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack vex_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack vex_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack vex_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack vex_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack vex_origin_info7 = new ItemStack(Material.FILLED_MAP);


        ItemMeta vex_origin_info1_meta = vex_origin_info1.getItemMeta();
        vex_origin_info1_meta.setDisplayName(UNDERLINE + "Weightless Soul");
        ArrayList<String> vex_origin_info1_lore = new ArrayList<>();
        vex_origin_info1_lore.add(WHITE + "You can fly. Really fast..");
        vex_origin_info1_meta.setLore(vex_origin_info1_lore);
        vex_origin_info1.setItemMeta(vex_origin_info1_meta);

        ItemMeta vex_origin_info2_meta = vex_origin_info2.getItemMeta();
        vex_origin_info2_meta.setDisplayName(UNDERLINE + "Raging Vex");
        ArrayList<String> vex_origin_info2_lore = new ArrayList<>();
        vex_origin_info2_lore.add(WHITE + "You gain strength 2 and speed 3 upon being hit for 2 seconds");
        vex_origin_info2_meta.setLore(vex_origin_info2_lore);
        vex_origin_info2.setItemMeta(vex_origin_info2_meta);

        ItemMeta vex_origin_info3_meta = vex_origin_info3.getItemMeta();
        vex_origin_info3_meta.setDisplayName(UNDERLINE + "Friends of the Raiders");
        ArrayList<String> vex_origin_info3_lore = new ArrayList<>();
        vex_origin_info3_lore.add(WHITE + "Pillagers will not attack you, but Iron Golems will");
        vex_origin_info3_meta.setLore(vex_origin_info3_lore);
        vex_origin_info3.setItemMeta(vex_origin_info3_meta);

        ItemMeta vex_origin_info4_meta = vex_origin_info4.getItemMeta();
        vex_origin_info4_meta.setDisplayName(UNDERLINE + "Feared Spirit");
        ArrayList<String> vex_origin_info4_lore = new ArrayList<>();
        vex_origin_info4_lore.add(WHITE + "Villagers will not trade with you");
        vex_origin_info4_meta.setLore(vex_origin_info4_lore);
        vex_origin_info4.setItemMeta(vex_origin_info4_meta);

        ItemMeta vex_origin_info5_meta = vex_origin_info5.getItemMeta();
        vex_origin_info5_meta.setDisplayName(UNDERLINE + "Unholy Creature");
        ArrayList<String> vex_origin_info5_lore = new ArrayList<>();
        vex_origin_info5_lore.add(WHITE + "You cannot wear armour made of iron, gold, or chainmail");
        vex_origin_info5_meta.setLore(vex_origin_info5_lore);
        vex_origin_info5.setItemMeta(vex_origin_info5_meta);

        ItemMeta vex_origin_info6_meta = vex_origin_info6.getItemMeta();
        vex_origin_info6_meta.setDisplayName(UNDERLINE + "Bloodlust");
        ArrayList<String> vex_origin_info6_lore = new ArrayList<>();
        vex_origin_info6_lore.add(WHITE + "You can only eat raw meat");
        vex_origin_info6_meta.setLore(vex_origin_info6_lore);
        vex_origin_info6.setItemMeta(vex_origin_info6_meta);

        ItemMeta vex_origin_info7_meta = vex_origin_info7.getItemMeta();
        vex_origin_info7_meta.setDisplayName(UNDERLINE + "Little Demon");
        ArrayList<String> vex_origin_info7_lore = new ArrayList<>();
        vex_origin_info7_lore.add(WHITE + "You are slower and MUCH weaker when in water");
        vex_origin_info7_meta.setLore(vex_origin_info7_lore);
        vex_origin_info7.setItemMeta(vex_origin_info7_meta);

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

        ItemMeta vex_meta = vex.getItemMeta();
        vex_meta.setDisplayName("Vexian");
        ArrayList<String> vex_lore = new ArrayList<>();
        vex_lore.add(AQUA + "Vex Origin");
        vex_meta.setLore(vex_lore);
        vex.setItemMeta(vex_meta);


        ItemStack[] vexgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, vex, air, air, air, air, air, air, vex_origin_info1, vex_origin_info2, vex_origin_info3, vex_origin_info4, vex_origin_info5, air, air, air, air, vex_origin_info6, vex_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return vexgui_items;
    }
}
