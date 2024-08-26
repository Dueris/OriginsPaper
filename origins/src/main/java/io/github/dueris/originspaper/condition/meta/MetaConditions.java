package io.github.dueris.originspaper.condition.meta;

import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MetaConditions {

	public static <T> void register(RegistryKey<ConditionTypeFactory<T>> conditionDataType, @NotNull Consumer<ConditionTypeFactory<T>> factoryConsumer) {
		factoryConsumer.accept(AndCondition.getFactory(conditionDataType));
		factoryConsumer.accept(ChanceCondition.getFactory());
		factoryConsumer.accept(ConstantCondition.getFactory());
		factoryConsumer.accept(OrCondition.getFactory(conditionDataType));
	}
}
