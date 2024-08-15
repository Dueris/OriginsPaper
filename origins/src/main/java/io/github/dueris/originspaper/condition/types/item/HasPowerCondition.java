package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HasPowerCondition {

	public static boolean condition(SerializableData.Instance data, Tuple<Level, ItemStack> worldAndStack) {
		return false;

	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("has_power"),
			SerializableData.serializableData()
				.add("slot", SerializableDataTypes.EQUIPMENT_SLOT, null)
				.add("power", SerializableDataTypes.IDENTIFIER),
			HasPowerCondition::condition
		);
	}

}
