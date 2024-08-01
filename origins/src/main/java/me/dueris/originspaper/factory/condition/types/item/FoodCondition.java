package me.dueris.originspaper.factory.condition.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FoodCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().has(DataComponents.FOOD);
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("food"),
			InstanceDefiner.instanceDefiner(),
			FoodCondition::condition
		);
	}

}
