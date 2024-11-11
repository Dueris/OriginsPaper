package io.github.dueris.originspaper.data;

import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.registry.DataObjectFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface TypedDataObjectFactory<T> extends DataObjectFactory<T> {

	CompoundSerializableDataType<T> getDataType();

	static <T> @NotNull TypedDataObjectFactory<T> simple(SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		CompoundSerializableDataType<T> dataType = SerializableDataType.compound(serializableData, fromData, toData);
		return new TypedDataObjectFactory<>() {

			@Override
			public CompoundSerializableDataType<T> getDataType() {
				return dataType;
			}

			@Override
			public SerializableData getSerializableData() {
				return serializableData;
			}

			@Override
			public T fromData(SerializableData.Instance data) {
				return fromData.apply(data);
			}

			@Override
			public SerializableData.Instance toData(T t, SerializableData serializableData) {
				return toData.apply(t, serializableData);
			}

		};
	}

}
