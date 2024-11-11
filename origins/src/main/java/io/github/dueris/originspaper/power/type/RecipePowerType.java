package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.PowerManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipePowerType extends PowerType implements Prioritized<RecipePowerType> {
	public static List<ResourceLocation> registeredRecipes = new LinkedList<>();

	public static final TypedDataObjectFactory<RecipePowerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("recipe", ApoliDataTypes.DISALLOWING_INTERNAL_CRAFTING_RECIPE)
			.add("priority", SerializableDataTypes.INT, 0),
		data -> new RecipePowerType(
			data.get("recipe"),
			data.get("priority")
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("recipe", powerType.getRecipe())
			.set("priority", powerType.getPriority())
	);

	private final CraftingRecipe recipe;
	private final int priority;

	public RecipePowerType(CraftingRecipe recipe, int priority) {
		this.recipe = recipe;
		this.priority = priority;
	}

	public static void registerAll() {
		for (Power power : PowerManager.values()) {

			if (!(power.getPowerType() instanceof RecipePowerType recipePowerType)) {
				continue;
			}

			ResourceLocation powerId = power.getId();
			CraftingRecipe craftingRecipe = recipePowerType.getRecipe();
			NamespacedKey bukkitId = CraftNamespacedKey.fromMinecraft(powerId);

			Bukkit.getServer().addRecipe(craftingRecipe.toBukkitRecipe(bukkitId));
			registeredRecipes.add(powerId);

		}
		Bukkit.updateRecipes();
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.RECIPE;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public CraftingRecipe getRecipe() {
		return recipe;
	}

}
