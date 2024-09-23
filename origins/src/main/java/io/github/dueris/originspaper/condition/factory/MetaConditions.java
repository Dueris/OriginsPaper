package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.condition.type.meta.AllOfConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfConditionType;

import java.util.function.Consumer;

public class MetaConditions {

	public static <T> void register(SerializableDataType<ConditionTypeFactory<T>> dataType, Consumer<ConditionTypeFactory<T>> registrant) {
		registrant.accept(AllOfConditionType.getFactory(dataType));
		registrant.accept(ConstantConditionType.getFactory());
		registrant.accept(AnyOfConditionType.getFactory(dataType));
		registrant.accept(RandomChanceConditionType.getFactory());
	}
}
