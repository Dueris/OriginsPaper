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

public class MerlingContents {
    public static @Nullable ItemStack @NotNull [] MerlingContents(){
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
        ItemStack mermaid = new ItemStack(Material.COD);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack mermaid_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack mermaid_origin_info6 = new ItemStack(Material.FILLED_MAP);


        ItemMeta mermaid_origin_info1_meta = mermaid_origin_info1.getItemMeta();
        mermaid_origin_info1_meta.setDisplayName(UNDERLINE + "Gills");
        ArrayList<String> mermaid_origin_info1_lore = new ArrayList<>();
        mermaid_origin_info1_lore.add(WHITE + "You can ONLY breathe underwater, when raining, you can breathe on land for a short time");
        mermaid_origin_info1_meta.setLore(mermaid_origin_info1_lore);
        mermaid_origin_info1.setItemMeta(mermaid_origin_info1_meta);

        ItemMeta mermaid_origin_info2_meta = mermaid_origin_info2.getItemMeta();
        mermaid_origin_info2_meta.setDisplayName(UNDERLINE + "Wet Eyes");
        ArrayList<String> mermaid_origin_info2_lore = new ArrayList<>();
        mermaid_origin_info2_lore.add(WHITE + "Your vision underwater is nearly perfect");
        mermaid_origin_info2_meta.setLore(mermaid_origin_info2_lore);
        mermaid_origin_info2.setItemMeta(mermaid_origin_info2_meta);

        ItemMeta mermaid_origin_info3_meta = mermaid_origin_info3.getItemMeta();
        mermaid_origin_info3_meta.setDisplayName(UNDERLINE + "Opposing Forces");
        ArrayList<String> mermaid_origin_info3_lore = new ArrayList<>();
        mermaid_origin_info3_lore.add(WHITE + "You take significantly more damage from fire");
        mermaid_origin_info3_meta.setLore(mermaid_origin_info3_lore);
        mermaid_origin_info3.setItemMeta(mermaid_origin_info3_meta);

        ItemMeta mermaid_origin_info4_meta = mermaid_origin_info4.getItemMeta();
        mermaid_origin_info4_meta.setDisplayName(UNDERLINE + "Fins");
        ArrayList<String> mermaid_origin_info4_lore = new ArrayList<>();
        mermaid_origin_info4_lore.add(WHITE + "You can swim much faster underwater, and don't sink underwater");
        mermaid_origin_info4_meta.setLore(mermaid_origin_info4_lore);
        mermaid_origin_info4.setItemMeta(mermaid_origin_info4_meta);

        ItemMeta mermaid_origin_info5_meta = mermaid_origin_info5.getItemMeta();
        mermaid_origin_info5_meta.setDisplayName(UNDERLINE + "please don't");
        ArrayList<String> mermaid_origin_info5_lore = new ArrayList<>();
        mermaid_origin_info5_lore.add(WHITE + "don't eat fish, its basically cannabalism and thats gross. It gives you nausea.");
        mermaid_origin_info5_meta.setLore(mermaid_origin_info5_lore);
        mermaid_origin_info5.setItemMeta(mermaid_origin_info5_meta);

        ItemMeta mermaid_origin_info6_meta = mermaid_origin_info6.getItemMeta();
        mermaid_origin_info6_meta.setDisplayName(UNDERLINE + "Luck of the Sea");
        ArrayList<String> mermaid_origin_info6_lore = new ArrayList<>();
        mermaid_origin_info6_lore.add(WHITE + "You have increased fishing luck.");
        mermaid_origin_info6_meta.setLore(mermaid_origin_info6_lore);
        mermaid_origin_info6.setItemMeta(mermaid_origin_info6_meta);

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

        ItemMeta mermaid_meta = mermaid.getItemMeta();
        mermaid_meta.setDisplayName("Merling");
        ArrayList<String> mermaid_lore = new ArrayList<>();
        mermaid_lore.add(BLUE + "Merling Origin");
        mermaid_meta.setLore(mermaid_lore);
        mermaid_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        mermaid.setItemMeta(mermaid_meta);


        ItemStack[] mermaidgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, mermaid, air, air, air, air, air, air, mermaid_origin_info1, mermaid_origin_info2, mermaid_origin_info3, mermaid_origin_info4, mermaid_origin_info5, air, air, air, air, mermaid_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return mermaidgui_items;
    }
}
