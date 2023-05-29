package me.dueris.genesismc.core.items;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import static me.dueris.genesismc.core.files.GenesisDataFiles.getOrbCon;

public class OrbOfOrigins {

    public static ItemStack orb;

    public static void init() {
        createOrb();
    }

    public static void createOrb() {
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.setCustomModelData(00002);
        meta.setDisplayName(GenesisDataFiles.getOrbCon().getString("name"));
        meta.setUnbreakable(true);
        meta.getCustomTagContainer().setCustomTag(new NamespacedKey(GenesisMC.getPlugin(), "origins"), ItemTagType.STRING, "orb_of_origin");
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        item.setItemMeta(meta);
        orb = item;

        try {
            //Shaped Recipe for ORB_OF_ORIGINS
            if (GenesisDataFiles.getOrbCon().getString("orb-of-origins-enabled").equalsIgnoreCase("true")) {
                ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft("orboforigins"), item);
                sr.shape("123",
                        "456",
                        "789");
                sr.setIngredient('1', Material.valueOf(getOrbCon().get("crafting.top.left").toString()));
                sr.setIngredient('2', Material.valueOf(getOrbCon().get("crafting.top.middle").toString()));
                sr.setIngredient('3', Material.valueOf(getOrbCon().get("crafting.top.right").toString()));
                sr.setIngredient('4', Material.valueOf(getOrbCon().get("crafting.middle.left").toString()));
                sr.setIngredient('5', Material.valueOf(getOrbCon().get("crafting.middle.middle").toString()));
                sr.setIngredient('6', Material.valueOf(getOrbCon().get("crafting.middle.right").toString()));
                sr.setIngredient('7', Material.valueOf(getOrbCon().get("crafting.bottom.left").toString()));
                sr.setIngredient('8', Material.valueOf(getOrbCon().get("crafting.bottom.middle").toString()));
                sr.setIngredient('9', Material.valueOf(getOrbCon().get("crafting.bottom.right").toString()));
                Bukkit.getServer().addRecipe(sr);
            }
        } catch (Exception exception) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Failed to load custom origin orb recipe!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Either delete the file, or add a valid material/path.");
        }
    }
}
