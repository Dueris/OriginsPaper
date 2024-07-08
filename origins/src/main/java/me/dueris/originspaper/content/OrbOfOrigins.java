package me.dueris.originspaper.content;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.apoli.RecipePower;
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

import static me.dueris.originspaper.storage.OriginConfiguration.getOrbConfiguration;

public class OrbOfOrigins {

	public static ItemStack orb;

	public static void init() {
		createOrb();
	}

	private static void createOrb() {
		ItemStack item = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.setCustomModelData(0003);
		meta.setDisplayName("Orb of Origins");
		meta.setUnbreakable(true);
		meta.getCustomTagContainer().setCustomTag(new NamespacedKey(OriginsPaper.getPlugin(), "origins"), ItemTagType.STRING, "orb_of_origin");
		meta.addEnchant(Enchantment.INFINITY, 1, true);
		item.setItemMeta(meta);

		try {
			//Shaped Recipe for ORB_OF_ORIGINS
			if (OriginConfiguration.getConfiguration().getString("orb-of-origins").equalsIgnoreCase("true")) {
				ShapedRecipe sr = new ShapedRecipe(new NamespacedKey("origins", "orb_of_origins"), item);
				sr.shape("123",
					"456",
					"789");
				sr.setIngredient('1', Material.valueOf(getOrbConfiguration().get("crafting.top.left").toString()));
				sr.setIngredient('2', Material.valueOf(getOrbConfiguration().get("crafting.top.middle").toString()));
				sr.setIngredient('3', Material.valueOf(getOrbConfiguration().get("crafting.top.right").toString()));
				sr.setIngredient('4', Material.valueOf(getOrbConfiguration().get("crafting.middle.left").toString()));
				sr.setIngredient('5', Material.valueOf(getOrbConfiguration().get("crafting.middle.middle").toString()));
				sr.setIngredient('6', Material.valueOf(getOrbConfiguration().get("crafting.middle.right").toString()));
				sr.setIngredient('7', Material.valueOf(getOrbConfiguration().get("crafting.bottom.left").toString()));
				sr.setIngredient('8', Material.valueOf(getOrbConfiguration().get("crafting.bottom.middle").toString()));
				sr.setIngredient('9', Material.valueOf(getOrbConfiguration().get("crafting.bottom.right").toString()));
				Bukkit.getServer().addRecipe(sr);
				orb = sr.getResult().clone();

				RecipePower.taggedRegistry.put(sr.key().asString(), sr);
			}
		} catch (Exception exception) {
			Bukkit.getServer().getLogger().warning("An unexpected error occured when trying to load the orb of origins! : " + exception.getLocalizedMessage());
			exception.printStackTrace();
		}
	}
}
