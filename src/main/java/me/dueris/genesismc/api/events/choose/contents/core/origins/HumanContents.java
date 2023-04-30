package me.dueris.genesismc.api.events.choose.contents.core.origins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.WHITE;

public class HumanContents {

    public static ItemStack[] HumanContents(){
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
        ItemStack human = new ItemStack(Material.PLAYER_HEAD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack human_origin_info = new ItemStack(Material.FILLED_MAP);
        ItemStack blank = new ItemStack(Material.PAPER);


        ItemMeta human_origin_info_meta = human_origin_info.getItemMeta();
        human_origin_info_meta.setDisplayName("Nothing");
        ArrayList<String> human_origin_info_lore = new ArrayList<>();
        human_origin_info_lore.add(WHITE + "Enough said.");
        human_origin_info_meta.setLore(human_origin_info_lore);
        human_origin_info.setItemMeta(human_origin_info_meta);

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

        ItemMeta human_meta = human.getItemMeta();
        human_meta.setDisplayName("Human");
        ArrayList<String> human_lore = new ArrayList<>();
        human_lore.add(WHITE + "Human Origin");
        human_meta.addEnchant(Enchantment.ARROW_INFINITE, 1,true);
        human_meta.setLore(human_lore);
        human.setItemMeta(human_meta);


        ItemStack[] humangui_items = {close, air, air, air, orb, air, air, air, close,
                air, air, air, air, human, air, air, air, air, air, air,
                blank, blank, human_origin_info, blank, blank, air, air,
                air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return humangui_items;

    }


}
