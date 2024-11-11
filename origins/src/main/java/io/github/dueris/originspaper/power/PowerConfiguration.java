package io.github.dueris.originspaper.power;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.TypeConfiguration;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public record PowerConfiguration<T extends PowerType>(ResourceLocation id, TypedDataObjectFactory<T> dataFactory) implements TypeConfiguration<T> {

	public static <T extends PowerType> PowerConfiguration<T> of(ResourceLocation id, SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return dataFactory(id, TypedDataObjectFactory.simple(serializableData, fromData, toData));
	}

	public static <T extends PowerType> PowerConfiguration<T> conditionedOf(ResourceLocation id, SerializableData serializableData, BiFunction<SerializableData.Instance, Optional<EntityCondition>,  T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return dataFactory(id, PowerType.createConditionedDataFactory(serializableData, fromData, toData));
	}

	public static <T extends PowerType> PowerConfiguration<T> dataFactory(ResourceLocation id, TypedDataObjectFactory<T> dataFactory) {
		return new PowerConfiguration<>(id, dataFactory);
	}

	public static <T extends PowerType> PowerConfiguration<T> simple(ResourceLocation id, Supplier<T> constructor) {
		return of(id, new SerializableData(), data -> constructor.get(), (t, serializableData) -> serializableData.instance());
	}

	public static <T extends PowerType> PowerConfiguration<T> conditionedSimple(ResourceLocation id, Function<Optional<EntityCondition>, T> constructor) {
		return conditionedOf(id, new SerializableData(), (data, entityCondition) -> constructor.apply(entityCondition), (t, serializableData) -> serializableData.instance());
	}

}
