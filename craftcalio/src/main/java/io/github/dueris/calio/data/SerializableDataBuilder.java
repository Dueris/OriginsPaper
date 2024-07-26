package io.github.dueris.calio.data;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface SerializableDataBuilder<T> {
	static <T> @NotNull SerializableDataBuilder<T> of(Function<JsonElement, T> deserialize, Class<?> type) {
		return new SerializableDataBuilder<T>() {
			@Override
			public T deserialize(JsonElement object) {
				return deserialize.apply(object);
			}

			@Override
			public Class<?> type() {
				return type;
			}
		};
	}

	T deserialize(JsonElement object);

	Class<?> type();
}
