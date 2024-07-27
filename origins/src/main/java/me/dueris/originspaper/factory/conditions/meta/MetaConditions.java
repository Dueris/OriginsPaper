package me.dueris.originspaper.factory.conditions.meta;

import io.github.dueris.calio.registry.RegistryKey;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MetaConditions {

	public static <T> void register(RegistryKey<ConditionFactory<T>> conditionDataType, @NotNull Consumer<ConditionFactory<T>> factoryConsumer) {
		factoryConsumer.accept(AndCondition.getFactory(conditionDataType));
		factoryConsumer.accept(ChanceCondition.getFactory());
		factoryConsumer.accept(ConstantCondition.getFactory());
		factoryConsumer.accept(OrCondition.getFactory(conditionDataType));
	}
}
