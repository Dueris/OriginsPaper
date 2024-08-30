package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class IngredientConditionType {

	public static boolean condition(ItemStack stack, Ingredient ingredient) {
		return ingredient.test(stack);
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("ingredient"),
			new SerializableData()
				.add("ingredient", SerializableDataTypes.INGREDIENT),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("ingredient")
			)
		);
	}

}
