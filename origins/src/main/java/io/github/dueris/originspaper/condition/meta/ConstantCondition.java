package io.github.dueris.originspaper.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

public class ConstantCondition {

	public static <T> @NotNull ConditionTypeFactory<T> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("constant"),
			SerializableData.serializableData()
				.add("value", SerializableDataTypes.BOOLEAN),
			(data, t) -> {
				return data.getBoolean("value");
			}
		);
	}
}
