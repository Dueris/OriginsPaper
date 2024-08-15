package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FoodCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().has(DataComponents.FOOD);
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("food"),
			SerializableData.serializableData(),
			FoodCondition::condition
		);
	}

}
