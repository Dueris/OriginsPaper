package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class IngredientCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {
		return data.<Ingredient>get("ingredient").test(worldAndStack.getB());
	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("ingredient"),
			InstanceDefiner.instanceDefiner()
				.add("ingredient", SerializableDataTypes.INGREDIENT),
			IngredientCondition::condition
		);
	}

}
