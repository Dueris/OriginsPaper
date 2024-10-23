package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.meta.AllOfConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceConditionType;

import java.util.function.Consumer;

public class MetaConditionTypes {

	public static <T> void register(SerializableDataType<ConditionTypeFactory<T>.Instance> dataType, Consumer<ConditionTypeFactory<T>> registrant) {
		registrant.accept(AllOfConditionType.getFactory(dataType));
		registrant.accept(ConstantConditionType.getFactory());
		registrant.accept(AnyOfConditionType.getFactory(dataType));
		registrant.accept(RandomChanceConditionType.getFactory());
	}

}
