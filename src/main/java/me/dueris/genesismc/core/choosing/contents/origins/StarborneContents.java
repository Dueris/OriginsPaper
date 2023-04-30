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

public class StarborneContents {
    public static @Nullable ItemStack @NotNull [] StarborneContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack star = new ItemStack(Material.NETHER_STAR);
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
        ItemStack star_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info5 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info6 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info7 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info8 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info9 = new ItemStack(Material.FILLED_MAP);
        ItemStack star_origin_info10 = new ItemStack(Material.FILLED_MAP);


        ItemMeta star_origin_info1_meta = star_origin_info1.getItemMeta();
        star_origin_info1_meta.setDisplayName(UNDERLINE + "Wanderer of the Stars");
        ArrayList<String> star_origin_info1_lore = new ArrayList<>();
        star_origin_info1_lore.add(WHITE + "You cannot sleep at night");
        star_origin_info1_meta.setLore(star_origin_info1_lore);
        star_origin_info1.setItemMeta(star_origin_info1_meta);

        ItemMeta star_origin_info2_meta = star_origin_info2.getItemMeta();
        star_origin_info2_meta.setDisplayName(UNDERLINE + "Shooting Star");
        ArrayList<String> star_origin_info2_lore = new ArrayList<>();
        star_origin_info2_lore.add(WHITE + "You can fling yourself into the air after a 5 second cooldown");
        star_origin_info2_meta.setLore(star_origin_info2_lore);
        star_origin_info2.setItemMeta(star_origin_info2_meta);

        ItemMeta star_origin_info3_meta = star_origin_info3.getItemMeta();
        star_origin_info3_meta.setDisplayName(UNDERLINE + "Falling Stars");
        ArrayList<String> star_origin_info3_lore = new ArrayList<>();
        star_origin_info3_lore.add(WHITE + "You can drop stars on your enemy every 30 seconds");
        star_origin_info3_meta.setLore(star_origin_info3_lore);
        star_origin_info3.setItemMeta(star_origin_info3_meta);

        ItemMeta star_origin_info4_meta = star_origin_info4.getItemMeta();
        star_origin_info4_meta.setDisplayName(UNDERLINE + "Mysterious Power");
        ArrayList<String> star_origin_info4_lore = new ArrayList<>();
        star_origin_info4_lore.add(WHITE + "When night falls, you have will be granted a special gift from the stars above");
        star_origin_info4_meta.setLore(star_origin_info4_lore);
        star_origin_info4.setItemMeta(star_origin_info4_meta);

        ItemMeta star_origin_info5_meta = star_origin_info5.getItemMeta();
        star_origin_info5_meta.setDisplayName(UNDERLINE + "Supernova");
        ArrayList<String> star_origin_info5_lore = new ArrayList<>();
        star_origin_info5_lore.add(WHITE + "When you die, you explode into a supernova");
        star_origin_info5_meta.setLore(star_origin_info5_lore);
        star_origin_info5.setItemMeta(star_origin_info5_meta);

        ItemMeta star_origin_info6_meta = star_origin_info6.getItemMeta();
        star_origin_info6_meta.setDisplayName(UNDERLINE + "Cold Vacuum");
        ArrayList<String> star_origin_info6_lore = new ArrayList<>();
        star_origin_info6_lore.add(WHITE + "You are used to the coldness of space, so you take double damage from fire");
        star_origin_info6_meta.setLore(star_origin_info6_lore);
        star_origin_info6.setItemMeta(star_origin_info6_meta);

        ItemMeta star_origin_info7_meta = star_origin_info7.getItemMeta();
        star_origin_info7_meta.setDisplayName(UNDERLINE + "Stargazer");
        ArrayList<String> star_origin_info7_lore = new ArrayList<>();
        star_origin_info7_lore.add(WHITE + "When exposed to the stars, you gain speed and regeneration, as a gift from the stars");
        star_origin_info7_meta.setLore(star_origin_info7_lore);
        star_origin_info7.setItemMeta(star_origin_info7_meta);

        ItemMeta star_origin_info8_meta = star_origin_info8.getItemMeta();
        star_origin_info8_meta.setDisplayName(UNDERLINE + "Unknown Realms");
        ArrayList<String> star_origin_info8_lore = new ArrayList<>();
        star_origin_info8_lore.add(WHITE + "Being in a realm without stars makes you weaker");
        star_origin_info8_meta.setLore(star_origin_info8_lore);
        star_origin_info8.setItemMeta(star_origin_info8_meta);

        ItemMeta star_origin_info9_meta = star_origin_info9.getItemMeta();
        star_origin_info9_meta.setDisplayName(UNDERLINE + "Nonviolent");
        ArrayList<String> star_origin_info9_lore = new ArrayList<>();
        star_origin_info9_lore.add(WHITE + "You have a chance to be imobilized upon taking damage, and your a vegitarian");
        star_origin_info9_meta.setLore(star_origin_info9_lore);
        star_origin_info9.setItemMeta(star_origin_info9_meta);

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

        ItemMeta star_meta = star.getItemMeta();
        star_meta.setDisplayName("Starborne");
        ArrayList<String> star_lore = new ArrayList<>();
        star_lore.add(LIGHT_PURPLE + "Starborne Origin");
        star_meta.setLore(star_lore);
        star_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        star.setItemMeta(star_meta);


        ItemStack[] stargui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, star, air, air, air, air, air, air, star_origin_info1, star_origin_info2, star_origin_info3, star_origin_info4, star_origin_info5, air, air, air, air, star_origin_info6, star_origin_info7, star_origin_info8, star_origin_info9, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return stargui_items;
    }
}
