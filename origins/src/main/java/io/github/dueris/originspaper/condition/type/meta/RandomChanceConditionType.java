package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.RandomSource;

public class RandomChanceConditionType {

	public static boolean condition(float chance) {
		return RandomSource.create().nextFloat() < chance;
	}

	public static <T> ConditionTypeFactory<T> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("random_chance"),
			new SerializableData()
				.add("chance", SerializableDataTypes.FLOAT),
			(data, type) -> condition(
				data.get("chance")
			)
		);
	}

}
