package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;

import java.util.Collection;
import java.util.function.Predicate;

public class AllOfConditionType {

	public static <T> boolean condition(T type, Collection<Predicate<T>> conditions) {
		return conditions
			.stream()
			.allMatch(condition -> condition.test(type));
	}

	public static <T> ConditionTypeFactory<T> getFactory(SerializableDataType<ConditionTypeFactory<T>.Instance> conditionDataType) {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("all_of"),
			new SerializableData()
				.add("conditions", conditionDataType.list()),
			(data, type) -> condition(type,
				data.get("conditions")
			)
		);
	}

}
