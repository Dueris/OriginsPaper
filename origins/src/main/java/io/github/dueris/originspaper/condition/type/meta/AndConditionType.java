package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

public class AndConditionType {

	public static <T> boolean condition(T type, @NotNull Collection<Predicate<T>> conditions) {
		return conditions
			.stream()
			.allMatch(condition -> condition.test(type));
	}

	public static <T> @NotNull ConditionTypeFactory<T> getFactory(@NotNull SerializableDataType<ConditionTypeFactory<T>> conditionDataType) {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("and"),
			new SerializableData()
				.add("conditions", SerializableDataType.of(conditionDataType.listOf())),
			(data, type) -> condition(type,
				data.get("conditions")
			)
		);
	}

}
