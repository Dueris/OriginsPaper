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

public class RabbitContents {
    public static @Nullable ItemStack @NotNull [] RabbitContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
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

        ItemMeta menumeta = menu.getItemMeta();
        menumeta.setDisplayName(ChatColor.AQUA + "Return");
        menumeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> menulore = new ArrayList<>();
        menumeta.setLore(menulore);
        menu.setItemMeta(menumeta);

        ItemMeta rabbit_meta = rabbit.getItemMeta();
        rabbit_meta.setDisplayName("Rabbit");
        ArrayList<String> rabbit_lore = new ArrayList<>();
        rabbit_lore.add(GOLD + "Bunny Origin");
        rabbit_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        rabbit_meta.setLore(rabbit_lore);
        rabbit.setItemMeta(rabbit_meta);


        ItemStack[] rabbitgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, rabbit, air, air, air, air, air, air, rabbit_origin_info1, rabbit_origin_info2, rabbit_origin_info3, rabbit_origin_info4, rabbit_origin_info5, air, air, air, air, rabbit_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return rabbitgui_items;
    }
}
