package io.github.dueris.calio.registry;

import io.github.dueris.calio.data.SerializableData;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleDataObjectFactory<T> implements DataObjectFactory<T> {

	private final SerializableData serializableData;

	private final Function<SerializableData.Instance, T> fromData;
	private final BiFunction<T, SerializableData, SerializableData.Instance> toData;

	public SimpleDataObjectFactory(SerializableData serializableData, Function<SerializableData.Instance, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		this.serializableData = serializableData;
		this.fromData = fromData;
		this.toData = toData;
	}

	@Override
	public SerializableData getSerializableData() {
		return serializableData;
	}

	@Override
	public T fromData(SerializableData.Instance instance) {
		return fromData.apply(instance);
	}

	@Override
	public SerializableData.Instance toData(T t, SerializableData serializableData) {
		return toData.apply(t, serializableData);
	}

}
