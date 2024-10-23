package io.github.dueris.calio.util.registry;

import io.github.dueris.calio.data.SerializableData;

public interface DataObjectFactory<T> {

	SerializableData getSerializableData();

	T fromData(SerializableData.Instance instance);

	SerializableData.Instance toData(T t, SerializableData serializableData);

	default SerializableData.Instance toData(T t) {
		return toData(t, getSerializableData());
	}

}
