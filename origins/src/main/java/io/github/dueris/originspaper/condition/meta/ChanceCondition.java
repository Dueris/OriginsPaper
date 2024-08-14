package io.github.dueris.originspaper.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChanceCondition {
	private static final Random random = new Random();

	public static <T> @NotNull ConditionFactory<T> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("chance"),
			InstanceDefiner.instanceDefiner()
				.add("chance", SerializableDataTypes.FLOAT),
			(data, t) -> {
				return random.nextFloat() < data.getFloat("chance");
			}
		);
	}
}
