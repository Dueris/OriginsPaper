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
import java.util.Arrays;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;

public class CreepContents {
    public static @Nullable ItemStack @NotNull [] CreepContents(){
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
        ItemStack creep = new ItemStack(Material.GUNPOWDER);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack creep_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack creep_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack creep_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack creep_origin_info4 = new ItemStack(Material.FILLED_MAP);


        ItemMeta creep_origin_info1_meta = creep_origin_info1.getItemMeta();
        creep_origin_info1_meta.setDisplayName(UNDERLINE + "BOOOOOM");
        creep_origin_info1_meta.setLore(Arrays.asList(WHITE + "You can explode at will,", WHITE + "but you take 5 hearts of damage"));
        creep_origin_info1.setItemMeta(creep_origin_info1_meta);

        ItemMeta creep_origin_info2_meta = creep_origin_info2.getItemMeta();
        creep_origin_info2_meta.setDisplayName(UNDERLINE + "Charged");
        ArrayList<String> creep_origin_info2_lore = new ArrayList<>();
        creep_origin_info2_lore.add(WHITE + "During thunderstorms, you are significantly stronger");
        creep_origin_info2_meta.setLore(creep_origin_info2_lore);
        creep_origin_info2.setItemMeta(creep_origin_info2_meta);

        ItemMeta creep_origin_info3_meta = creep_origin_info3.getItemMeta();
        creep_origin_info3_meta.setDisplayName(UNDERLINE + "You got a Friend in Me");
        ArrayList<String> creep_origin_info3_lore = new ArrayList<>();
        creep_origin_info3_lore.add(WHITE + "Other creepers will not attack you");
        creep_origin_info3_meta.setLore(creep_origin_info3_lore);
        creep_origin_info3.setItemMeta(creep_origin_info3_meta);

        ItemMeta creep_origin_info4_meta = creep_origin_info4.getItemMeta();
        creep_origin_info4_meta.setDisplayName(UNDERLINE + "Felinephobia");
        creep_origin_info4_meta.setLore(Arrays.asList(WHITE + "You are scared of cats and you", WHITE + "will take damage when you are close"));
        creep_origin_info4.setItemMeta(creep_origin_info4_meta);

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

        ItemMeta creep_meta = creep.getItemMeta();
        creep_meta.setDisplayName("Creep");
        ArrayList<String> creep_lore = new ArrayList<>();
        creep_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        creep_lore.add(GREEN + "Creeper Origin");
        creep_meta.setLore(creep_lore);
        creep.setItemMeta(creep_meta);


        ItemStack[] creepgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, creep, air, air, air, air, air, air, creep_origin_info1, creep_origin_info2, creep_origin_info3, creep_origin_info4, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return creepgui_items;
    }
}
