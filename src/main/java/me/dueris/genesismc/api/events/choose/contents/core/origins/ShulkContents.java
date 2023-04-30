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

public class ShulkContents {
    
    public static @Nullable ItemStack @NotNull [] ShulkContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
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
        ItemStack shulk_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack shulk_origin_info5 = new ItemStack(Material.FILLED_MAP);

        ItemMeta shulk_origin_info1_meta = shulk_origin_info1.getItemMeta();
        //Component.keybind("key.drop")
        shulk_origin_info1_meta.setDisplayName(UNDERLINE + "Hoarder" + DARK_GRAY + " (/shulk open, or press the F key to open)");
        shulk_origin_info1_meta.setLore(Arrays.asList(WHITE + "You have 9 extra inventory slots.", WHITE + "You keep these items upon death"));
        shulk_origin_info1.setItemMeta(shulk_origin_info1_meta);

        ItemMeta shulk_origin_info2_meta = shulk_origin_info2.getItemMeta();
        shulk_origin_info2_meta.setDisplayName(UNDERLINE + "Sturdy Skin");
        ArrayList<String> shulk_origin_info2_lore = new ArrayList<>();
        shulk_origin_info2_lore.add(WHITE + "Your skin has natural protection");
        shulk_origin_info2_meta.setLore(shulk_origin_info2_lore);
        shulk_origin_info2.setItemMeta(shulk_origin_info2_meta);

        ItemMeta shulk_origin_info3_meta = shulk_origin_info3.getItemMeta();
        shulk_origin_info3_meta.setDisplayName(UNDERLINE + "Strong Arms");
        ArrayList<String> shulk_origin_info3_lore = new ArrayList<>();
        shulk_origin_info3_lore.add(WHITE + "You can break natural stones without a pickaxe");
        shulk_origin_info3_meta.setLore(shulk_origin_info3_lore);
        shulk_origin_info3.setItemMeta(shulk_origin_info3_meta);

        ItemMeta shulk_origin_info4_meta = shulk_origin_info4.getItemMeta();
        shulk_origin_info4_meta.setDisplayName(UNDERLINE + "Unwieldy");
        ArrayList<String> shulk_origin_info4_lore = new ArrayList<>();
        shulk_origin_info4_lore.add(WHITE + "You cannot hold a shield");
        shulk_origin_info4_meta.setLore(shulk_origin_info4_lore);
        shulk_origin_info4.setItemMeta(shulk_origin_info4_meta);

        ItemMeta shulk_origin_info5_meta = shulk_origin_info5.getItemMeta();
        shulk_origin_info5_meta.setDisplayName(UNDERLINE + "Large Appetite");
        ArrayList<String> shulk_origin_info5_lore = new ArrayList<>();
        shulk_origin_info5_lore.add(WHITE + "You exhaust much quicker than others");
        shulk_origin_info5_meta.setLore(shulk_origin_info5_lore);
        shulk_origin_info5.setItemMeta(shulk_origin_info5_meta);

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

        ItemMeta shulk_meta = shulk.getItemMeta();
        shulk_meta.setDisplayName("Shulk");
        ArrayList<String> shulk_lore = new ArrayList<>();
        shulk_lore.add(LIGHT_PURPLE + "Shulker Origin");
        shulk_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        shulk_meta.setLore(shulk_lore);
        shulk.setItemMeta(shulk_meta);


        ItemStack[] shulkgui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, shulk, air, air, air, air, air, air, shulk_origin_info1, shulk_origin_info2, shulk_origin_info3, shulk_origin_info4, shulk_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return shulkgui_items;
    }
    
}
