package me.dueris.genesismc.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WaterProtItem {

    public static ItemStack enchbook;

    public static void init() {
        createBook();
    }

    public static void createBook() {
        ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Water Protection");
        meta.setLore(lore);
        item.setItemMeta(meta);
        enchbook = item;

    }
}