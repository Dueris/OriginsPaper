package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FireResistantCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().has(DataComponents.FIRE_RESISTANT);
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fire_resistant"),
			InstanceDefiner.instanceDefiner(),
			FireResistantCondition::condition
		);

	}

}
