package me.dueris.genesismc.core.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InfinPearl {

    public static ItemStack infinpearl;

    public static void init() {
        createPearl();
    }

    public static void createPearl() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(00001);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        List<String> lore = new ArrayList<>();
        item.setItemMeta(meta);
        infinpearl = item;
    }
}