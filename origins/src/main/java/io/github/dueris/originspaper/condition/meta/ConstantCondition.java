package io.github.dueris.originspaper.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
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
