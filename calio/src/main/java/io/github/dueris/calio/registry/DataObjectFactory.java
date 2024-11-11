package io.github.dueris.calio.registry;

import io.github.dueris.calio.data.SerializableData;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface DataObjectFactory<T> {

	SerializableData getSerializableData();
	T fromData(SerializableData.Instance instance);

	SerializableData.Instance toData(T t, SerializableData serializableData);
	default SerializableData.Instance toData(T t) {
		return toData(t, getSerializableData());
	}

	static <T> DataObjectFactory<T> simple(SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return new DataObjectFactory<>() {

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
