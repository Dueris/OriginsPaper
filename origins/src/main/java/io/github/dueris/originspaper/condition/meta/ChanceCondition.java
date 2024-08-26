package io.github.dueris.originspaper.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChanceCondition {
	private static final Random random = new Random();

	public static <T> @NotNull ConditionTypeFactory<T> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("chance"),
			SerializableData.serializableData()
				.add("chance", SerializableDataTypes.FLOAT),
			(data, t) -> {
				return random.nextFloat() < data.getFloat("chance");
			}
		);
	}
}
