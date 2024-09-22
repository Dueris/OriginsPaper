package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class ChanceConditionType {

	public static boolean condition(float chance) {
		return RandomSource.create().nextFloat() < chance;
	}

	public static <T> @NotNull ConditionTypeFactory<T> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("chance"),
			new SerializableData()
				.add("chance", SerializableDataTypes.FLOAT),
			(data, type) -> condition(
				data.get("chance")
			)
		);
	}

}
