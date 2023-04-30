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

public class ElytrianContents {
    public static @Nullable ItemStack @NotNull [] ElytrianContents(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack menu = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack elyrtian = new ItemStack(Material.ELYTRA);
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
        ItemStack elyrtian_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack elyrtian_origin_info5 = new ItemStack(Material.FILLED_MAP);


        ItemMeta elyrtian_origin_info1_meta = elyrtian_origin_info1.getItemMeta();
        elyrtian_origin_info1_meta.setDisplayName(UNDERLINE + "Winged");
        ArrayList<String> elyrtian_origin_info1_lore = new ArrayList<>();
        elyrtian_origin_info1_lore.add(WHITE + "You have Elytra wings without needing to equip any");
        elyrtian_origin_info1_meta.setLore(elyrtian_origin_info1_lore);
        elyrtian_origin_info1.setItemMeta(elyrtian_origin_info1_meta);

        ItemMeta elyrtian_origin_info2_meta = elyrtian_origin_info2.getItemMeta();
        elyrtian_origin_info2_meta.setDisplayName(UNDERLINE + "Gift of the Winds");
        ArrayList<String> elyrtian_origin_info2_lore = new ArrayList<>();
        elyrtian_origin_info2_lore.add(WHITE + "Every 60 seconds, you can launch yourself 20 blocks in the air");
        elyrtian_origin_info2_meta.setLore(elyrtian_origin_info2_lore);
        elyrtian_origin_info2.setItemMeta(elyrtian_origin_info2_meta);

        ItemMeta elyrtian_origin_info3_meta = elyrtian_origin_info3.getItemMeta();
        elyrtian_origin_info3_meta.setDisplayName(UNDERLINE + "Claustrophobia");
        ArrayList<String> elyrtian_origin_info3_lore = new ArrayList<>();
        elyrtian_origin_info3_lore.add(WHITE + "Being somewhere with a low ceiling for too long will weaken you");
        elyrtian_origin_info3_meta.setLore(elyrtian_origin_info3_lore);
        elyrtian_origin_info3.setItemMeta(elyrtian_origin_info3_meta);

        ItemMeta elyrtian_origin_info4_meta = elyrtian_origin_info4.getItemMeta();
        elyrtian_origin_info4_meta.setDisplayName(UNDERLINE + "Need for Mobility");
        ArrayList<String> elyrtian_origin_info4_lore = new ArrayList<>();
        elyrtian_origin_info4_lore.add(WHITE + "You cannot wear any heavy armour with prot values higher than chainmail");
        elyrtian_origin_info4_meta.setLore(elyrtian_origin_info4_lore);
        elyrtian_origin_info4.setItemMeta(elyrtian_origin_info4_meta);

        ItemMeta elyrtian_origin_info5_meta = elyrtian_origin_info5.getItemMeta();
        elyrtian_origin_info5_meta.setDisplayName(UNDERLINE + "Brittle Bones");
        ArrayList<String> elyrtian_origin_info5_lore = new ArrayList<>();
        elyrtian_origin_info5_lore.add(WHITE + "You take more damage from falling and flying into blocks");
        elyrtian_origin_info5_meta.setLore(elyrtian_origin_info5_lore);
        elyrtian_origin_info5.setItemMeta(elyrtian_origin_info5_meta);

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

        ItemMeta elyrtian_meta = elyrtian.getItemMeta();
        elyrtian_meta.setDisplayName("Elytrian");
        ArrayList<String> elyrtian_lore = new ArrayList<>();
        elyrtian_lore.add(GRAY + "Elytrian Origin");
        elyrtian_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        elyrtian_meta.setLore(elyrtian_lore);
        elyrtian.setItemMeta(elyrtian_meta);


        ItemStack[] elyrtiangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, elyrtian, air, air, air, air, air, air, elyrtian_origin_info1, elyrtian_origin_info2, elyrtian_origin_info3, elyrtian_origin_info4, elyrtian_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, menu, air, air, air, air};

        return elyrtiangui_items;
    }
}
