package me.dueris.originspaper.factory.conditions.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.registry.RegistryKey;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndCondition {

	public static <T> @NotNull ConditionFactory<T> getFactory(RegistryKey<ConditionFactory<T>> conditionDataType) {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("and"),
			InstanceDefiner.instanceDefiner()
				.add("conditions", SerializableDataTypes.list(ApoliDataTypes.condition(conditionDataType))),
			(data, t) -> {
				List<ConditionFactory<T>> conditions = data.get("conditions");
				return conditions.stream().allMatch(condition -> condition.test(t));
			}
		);
	}
}
