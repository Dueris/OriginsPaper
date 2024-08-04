package me.dueris.originspaper.factory.condition.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.registry.RegistryKey;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrCondition {

	public static <T> @NotNull ConditionFactory<T> getFactory(RegistryKey<ConditionFactory<T>> conditionDataType) {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("or"),
			InstanceDefiner.instanceDefiner()
				.add("conditions", SerializableDataTypes.list(ApoliDataTypes.condition(conditionDataType))),
			(data, t) -> {
				List<ConditionFactory<T>> conditions = data.get("conditions");
				return conditions.stream().anyMatch(condition -> condition.test(t));
			}
		);
	}
}
