package me.dueris.originspaper.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
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
