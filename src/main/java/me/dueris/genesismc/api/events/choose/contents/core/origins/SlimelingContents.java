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

public class SlimelingContents {
    public static @Nullable ItemStack @NotNull [] SlimelingContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack slime = new ItemStack(Material.SLIME_BALL);
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
        ItemStack slime_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack slime_origin_info6 = new ItemStack(Material.FILLED_MAP);


        ItemMeta slime_origin_info1_meta = slime_origin_info1.getItemMeta();
        slime_origin_info1_meta.setDisplayName(UNDERLINE + "Bouncy");
        ArrayList<String> slime_origin_info1_lore = new ArrayList<>();
        slime_origin_info1_lore.add(WHITE + "You bounce on any block as if it were a slime block");
        slime_origin_info1_meta.setLore(slime_origin_info1_lore);
        slime_origin_info1.setItemMeta(slime_origin_info1_meta);

        ItemMeta slime_origin_info2_meta = slime_origin_info2.getItemMeta();
        slime_origin_info2_meta.setDisplayName(UNDERLINE + "Not Very Solid");
        ArrayList<String> slime_origin_info2_lore = new ArrayList<>();
        slime_origin_info2_lore.add(WHITE + "Upon being hit, you have a chance to split and create small slimes");
        slime_origin_info2_meta.setLore(slime_origin_info2_lore);
        slime_origin_info2.setItemMeta(slime_origin_info2_meta);

        ItemMeta slime_origin_info3_meta = slime_origin_info3.getItemMeta();
        slime_origin_info3_meta.setDisplayName(UNDERLINE + "Improved Jump");
        ArrayList<String> slime_origin_info3_lore = new ArrayList<>();
        slime_origin_info3_lore.add(WHITE + "You have an improved leap at the cost of movement speed");
        slime_origin_info3_meta.setLore(slime_origin_info3_lore);
        slime_origin_info3.setItemMeta(slime_origin_info3_meta);

        ItemMeta slime_origin_info4_meta = slime_origin_info4.getItemMeta();
        slime_origin_info4_meta.setDisplayName(UNDERLINE + "Great Leap");
        ArrayList<String> slime_origin_info4_lore = new ArrayList<>();
        slime_origin_info4_lore.add(WHITE + "Upon shifting for 4 seconds(nothing in hand), you leap in the direction you are looking");
        slime_origin_info4_meta.setLore(slime_origin_info4_lore);
        slime_origin_info4.setItemMeta(slime_origin_info4_meta);

        ItemMeta slime_origin_info5_meta = slime_origin_info5.getItemMeta();
        slime_origin_info5_meta.setDisplayName(UNDERLINE + "Slimy Skin");
        ArrayList<String> slime_origin_info5_lore = new ArrayList<>();
        slime_origin_info5_lore.add(WHITE + "You have the green translucent skin of a slime");
        slime_origin_info5_meta.setLore(slime_origin_info5_lore);
        slime_origin_info5.setItemMeta(slime_origin_info5_meta);

        ItemMeta slime_origin_info6_meta = slime_origin_info6.getItemMeta();
        slime_origin_info6_meta.setDisplayName(UNDERLINE + "Burnable");
        ArrayList<String> slime_origin_info6_lore = new ArrayList<>();
        slime_origin_info6_lore.add(WHITE + "You burn when in hotter biomes");
        slime_origin_info6_meta.setLore(slime_origin_info6_lore);
        slime_origin_info6.setItemMeta(slime_origin_info6_meta);

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


        ItemMeta slime_meta = slime.getItemMeta();
        slime_meta.setDisplayName("Slimeling");
        ArrayList<String> slime_lore = new ArrayList<>();
        slime_lore.add(GREEN + "Slime Origin");
        slime_meta.setLore(slime_lore);
        slime.setItemMeta(slime_meta);


        ItemStack[] slimegui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, slime, air, air, air, air, air, air, slime_origin_info1, slime_origin_info2, slime_origin_info3, slime_origin_info4, slime_origin_info5, air, air, air, air, slime_origin_info6, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return slimegui_items;
    }
}
