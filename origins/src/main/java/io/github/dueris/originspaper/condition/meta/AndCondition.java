package io.github.dueris.originspaper.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndCondition {

	public static <T> @NotNull ConditionFactory<T> getFactory(RegistryKey<ConditionFactory<T>> conditionDataType) {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("and"),
			SerializableData.serializableData()
				.add("conditions", SerializableDataTypes.list(ApoliDataTypes.condition(conditionDataType))),
			(data, t) -> {
				List<ConditionFactory<T>> conditions = data.get("conditions");
				return conditions.stream().allMatch(condition -> condition.test(t));
			}
		);
	}
}
