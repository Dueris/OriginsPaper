package io.github.dueris.originspaper.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomModelData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Objects;

public class ModItems {
	public static final ItemStack ORB_OF_ORIGINS;

	static {
		ORB_OF_ORIGINS = new ItemStack(Items.MAGMA_CREAM);
		ORB_OF_ORIGINS.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1001));
		ORB_OF_ORIGINS.set(DataComponents.CUSTOM_NAME, Component.translatable("item.origins.orb_of_origin"));
		ORB_OF_ORIGINS.set(DataComponents.RARITY, Rarity.RARE);
		ORB_OF_ORIGINS.set(DataComponents.MAX_STACK_SIZE, 1);
	}

	public static void register() {
	}

	public static void registerServer() {
		CraftServer server = MinecraftServer.getServer().server;
		ShapedRecipe recipe = new ShapedRecipe(Objects.requireNonNull(NamespacedKey.fromString("origins:orb_of_origin")), ORB_OF_ORIGINS.asBukkitMirror());
		recipe.shape("ABA", "BCB", "ABA");
		recipe.setIngredient('A', Material.NETHERITE_INGOT);
		recipe.setIngredient('B', Material.DIAMOND);
		recipe.setIngredient('C', Material.NETHER_STAR);
		server.addRecipe(recipe);
	}
}
