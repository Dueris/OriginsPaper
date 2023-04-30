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

public class PiglinContents {
    public static @Nullable ItemStack @NotNull [] PiglinContents(){
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
        ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack blank = new ItemStack(Material.PAPER);

        ItemStack piglin_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack piglin_origin_info5 = new ItemStack(Material.FILLED_MAP);


        ItemMeta piglin_origin_info1_meta = piglin_origin_info1.getItemMeta();
        piglin_origin_info1_meta.setDisplayName(UNDERLINE + "I like to be SHINY");
        ArrayList<String> piglin_origin_info1_lore = new ArrayList<>();
        piglin_origin_info1_lore.add(WHITE + "Golden tools deal extra damage and gold armour has more protection");
        piglin_origin_info1_meta.setLore(piglin_origin_info1_lore);
        piglin_origin_info1.setItemMeta(piglin_origin_info1_meta);

        ItemMeta piglin_origin_info2_meta = piglin_origin_info2.getItemMeta();
        piglin_origin_info2_meta.setDisplayName(UNDERLINE + "Friendly Frenemies");
        ArrayList<String> piglin_origin_info2_lore = new ArrayList<>();
        piglin_origin_info2_lore.add(WHITE + "Piglins won't attack you unless provoked, Brutes will still attack on sight");
        piglin_origin_info2_meta.setLore(piglin_origin_info2_lore);
        piglin_origin_info2.setItemMeta(piglin_origin_info2_meta);

        ItemMeta piglin_origin_info3_meta = piglin_origin_info3.getItemMeta();
        piglin_origin_info3_meta.setDisplayName(UNDERLINE + "Nether Dweller");
        ArrayList<String> piglin_origin_info3_lore = new ArrayList<>();
        piglin_origin_info3_lore.add(WHITE + "Your natural spawn is in the Nether and you can only eat meat");
        piglin_origin_info3_meta.setLore(piglin_origin_info3_lore);
        piglin_origin_info3.setItemMeta(piglin_origin_info3_meta);

        ItemMeta piglin_origin_info4_meta = piglin_origin_info4.getItemMeta();
        piglin_origin_info4_meta.setDisplayName(UNDERLINE + "Colder Realms");
        ArrayList<String> piglin_origin_info4_lore = new ArrayList<>();
        piglin_origin_info4_lore.add(WHITE + "When outside of the Nether, you zombify and become immune to fire and slower");
        piglin_origin_info4_meta.setLore(piglin_origin_info4_lore);
        piglin_origin_info4.setItemMeta(piglin_origin_info4_meta);

        ItemMeta piglin_origin_info5_meta = piglin_origin_info5.getItemMeta();
        piglin_origin_info5_meta.setDisplayName(UNDERLINE + "BLUE FIRE SPOOKY");
        ArrayList<String> piglin_origin_info5_lore = new ArrayList<>();
        piglin_origin_info5_lore.add(WHITE + "You are afraid of soul fire, becoming weak when near it");
        piglin_origin_info5_meta.setLore(piglin_origin_info5_lore);
        piglin_origin_info5.setItemMeta(piglin_origin_info5_meta);

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

        ItemMeta piglin_meta = piglin.getItemMeta();
        piglin_meta.setDisplayName("Piglin");
        ArrayList<String> piglin_lore = new ArrayList<>();
        piglin_lore.add(GOLD + "Piglin Origin");
        piglin_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        piglin_meta.setLore(piglin_lore);
        piglin.setItemMeta(piglin_meta);


        ItemStack[] piglingui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, piglin, air, air, air, air, air, air, piglin_origin_info1, piglin_origin_info2, piglin_origin_info3, piglin_origin_info4, piglin_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return piglingui_items;
    }
}
