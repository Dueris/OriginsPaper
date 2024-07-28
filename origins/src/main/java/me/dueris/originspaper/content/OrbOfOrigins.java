package me.dueris.originspaper.content;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.storage.OriginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;

public class OrbOfOrigins {
	public static ItemStack orb;

	public static void init() {
		if (OriginConfiguration.getConfiguration().getBoolean("orb-of-origins", false)) {
			orb = createOrb();
		}
	}

	public static @NotNull ItemStack createOrb() {
		ItemStack item = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.setCustomModelData(3);
		meta.setDisplayName("Orb of Origins");
		meta.setUnbreakable(true);
		meta.getCustomTagContainer().setCustomTag(new NamespacedKey(OriginsPaper.getPlugin(), "origins"), ItemTagType.STRING, "orb_of_origin");
		meta.addEnchant(Enchantment.INFINITY, 1, true);
		item.setItemMeta(meta);

		try {
			ShapedRecipe sr = new ShapedRecipe(new NamespacedKey("origins", "orb_of_origins"), item);
			sr.shape("123", "456", "789");
			sr.setIngredient('1', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.top.left").toString()));
			sr.setIngredient('2', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.top.middle").toString()));
			sr.setIngredient('3', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.top.right").toString()));
			sr.setIngredient('4', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.middle.left").toString()));
			sr.setIngredient('5', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.middle.middle").toString()));
			sr.setIngredient('6', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.middle.right").toString()));
			sr.setIngredient('7', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.bottom.left").toString()));
			sr.setIngredient('8', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.bottom.middle").toString()));
			sr.setIngredient('9', Material.valueOf(OriginConfiguration.getOrbConfiguration().get("crafting.bottom.right").toString()));

			try {
				Bukkit.getServer().addRecipe(sr);
			} catch (Throwable var4) {
			}

			// RecipePower.taggedRegistry.put(sr.key().asString(), sr);
			return sr.getResult().clone();
		} catch (Exception var5) {
			Bukkit.getServer().getLogger().warning("An unexpected error occured when trying to load the orb of origins! : " + var5.getLocalizedMessage());
			throw var5;
		}
	}
}
