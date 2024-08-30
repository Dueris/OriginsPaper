package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

public class ConstantConditionType {

	public static boolean condition(boolean value) {
		return value;
	}

	public static <T> @NotNull ConditionTypeFactory<T> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("constant"),
			new SerializableData()
				.add("value", SerializableDataTypes.BOOLEAN),
			(data, type) -> condition(
				data.get("value")
			)
		);
	}

}
