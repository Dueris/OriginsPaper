package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.condition.type.meta.AndConditionType;
import io.github.dueris.originspaper.condition.type.meta.ChanceConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantConditionType;
import io.github.dueris.originspaper.condition.type.meta.OrConditionType;

import java.util.function.Consumer;

public class MetaConditions {

	public static <T> void register(SerializableDataType<ConditionTypeFactory<T>> dataType, Consumer<ConditionTypeFactory<T>> registrant) {
		registrant.accept(AndConditionType.getFactory(dataType));
		registrant.accept(ConstantConditionType.getFactory());
		registrant.accept(OrConditionType.getFactory(dataType));
		registrant.accept(ChanceConditionType.getFactory());
	}
}
