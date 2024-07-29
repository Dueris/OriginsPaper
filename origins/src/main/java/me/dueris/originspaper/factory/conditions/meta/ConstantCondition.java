package me.dueris.originspaper.factory.conditions.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import org.jetbrains.annotations.NotNull;

public class ConstantCondition {

	public static <T> @NotNull ConditionFactory<T> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("constant"),
			InstanceDefiner.instanceDefiner()
				.add("value", SerializableDataTypes.BOOLEAN),
			(data, t) -> {
				return data.getBoolean("value");
			}
		);
	}
}
