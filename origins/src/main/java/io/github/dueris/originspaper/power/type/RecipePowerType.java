package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

import java.util.LinkedList;
import java.util.List;

public class RecipePowerType extends PowerType {
	public static List<ResourceLocation> registeredRecipes = new LinkedList<>();

	private final CraftingRecipe recipe;

	public RecipePowerType(Power power, LivingEntity entity, CraftingRecipe recipe) {
		super(power, entity);
		this.recipe = recipe;
	}

	public CraftingRecipe getRecipe() {
		return recipe;
	}

	public static void registerAll() {
		for (Power power : PowerManager.values()) {

			if (!(power.create(null) instanceof RecipePowerType recipePowerType)) {
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

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("recipe"),
			new SerializableData()
				.add("recipe", ApoliDataTypes.DISALLOWING_INTERNAL_CRAFTING_RECIPE),
			data -> (power, entity) -> new RecipePowerType(power, entity,
				data.get("recipe")
			)
		).allowCondition();
	}

}
