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

public class BeeContents {
    public static @Nullable ItemStack @NotNull [] BeeContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
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

        ItemMeta menumeta = menu.getItemMeta();
        menumeta.setDisplayName(ChatColor.AQUA + "Return");
        menumeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> menulore = new ArrayList<>();
        menumeta.setLore(menulore);
        menu.setItemMeta(menumeta);

        ItemMeta bee_meta = bee.getItemMeta();
        bee_meta.setDisplayName("Bumblebee");
        ArrayList<String> bee_lore = new ArrayList<>();
        bee_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bee_lore.add(YELLOW + "Bee Origin");
        bee_meta.setLore(bee_lore);
        bee.setItemMeta(bee_meta);


        ItemStack[] beegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, bee, air, air, air, air, air, air, bee_origin_info1, bee_origin_info2, bee_origin_info3, bee_origin_info4, bee_origin_info5, air, air, air, air, bee_origin_info6, bee_origin_info7, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return beegui_items;
    }
}
