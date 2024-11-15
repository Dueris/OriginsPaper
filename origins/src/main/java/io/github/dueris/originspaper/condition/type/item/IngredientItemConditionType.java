package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IngredientItemConditionType extends ItemConditionType {

	public static final TypedDataObjectFactory<IngredientItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("ingredient", SerializableDataTypes.INGREDIENT),
		data -> new IngredientItemConditionType(
			data.get("ingredient")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("ingredient", conditionType.ingredient)
	);

	private final Ingredient ingredient;

	public IngredientItemConditionType(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {
		return ingredient.test(stack);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.INGREDIENT;
	}

}
