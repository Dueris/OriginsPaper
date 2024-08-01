package me.dueris.originspaper.factory.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
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
