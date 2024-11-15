package io.github.dueris.originspaper.condition;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.type.AbstractConditionType;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.TypeConfiguration;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public record ConditionConfiguration<T extends AbstractConditionType<?, ?>>(ResourceLocation id,
																			TypedDataObjectFactory<T> dataFactory) implements TypeConfiguration<T> {

	public static <T extends AbstractConditionType<?, ?>> ConditionConfiguration<T> of(ResourceLocation id, SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		TypedDataObjectFactory<T> dataFactory = TypedDataObjectFactory.simple(serializableData, fromData, toData);
		return of(id, dataFactory);
	}

	public static <T extends AbstractConditionType<?, ?>> ConditionConfiguration<T> of(ResourceLocation id, TypedDataObjectFactory<T> dataFactory) {
		return new ConditionConfiguration<>(id, dataFactory);
	}

	public static <T extends AbstractConditionType<?, ?>> ConditionConfiguration<T> simple(ResourceLocation id, Supplier<T> constructor) {
		return of(id, new SerializableData(), data -> constructor.get(), (t, serializableData) -> serializableData.instance());
	}

}
