package io.github.dueris.calio.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.dueris.calio.parser.SerializableType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static io.github.dueris.calio.parser.CalioParser.LOGGER;

/**
 * The builder for parsing JsonElements -> Java objects
 * Upon deserializing, if a null value is provided, it will not parse. It WILL return null.
 */
// TODO - SHARE RESOURCELOCATION OF THE DATA BEING PARSED ? - Dueris
public interface SerializableDataType<T> extends Codec<T> {
	DynamicOps<JsonElement> JSON_OPS = JsonOps.INSTANCE;
	Logger log = LogManager.getLogger("SerializableDataBuilder");

	static <T> @NotNull SerializableDataType<List<T>> of(Codec<List<T>> listCodec) {
		return of(listCodec, List.class);
	}

	static <T> @NotNull SerializableDataType<T> of(Codec<T> codec, Class<?> type) {
		return new AbstractSerializableDataType<>(type) {
			@Override
			public T deserialize(JsonElement object) {
				if (object == null) return null;
				return decode(JSON_OPS, object).getOrThrow().getFirst();
			}

			@Override
			public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
				return codec.decode(ops, input);
			}
		};
	}

	static <T> @NotNull SerializableDataType<T> of(Function<JsonElement, T> deserialize, Class<?> type) {
		return of(deserialize, type, true);
	}

	static <T> @NotNull SerializableDataType<T> of(Function<JsonElement, T> deserialize, Class<?> type, boolean printJson) {
		return new AbstractSerializableDataType<>(type) {
			@Override
			public T deserialize(@Nullable JsonElement object) {
				if (object == null) return null;
				try {
					T dataResult = deserialize.apply(object);
					if (dataResult == null) {
						return SerializableDataType.returnNullThr(type, new Throwable(), printJson, object);
					}
					return dataResult;
				} catch (Throwable throwable) {
					return SerializableDataType.returnNullThr(type, throwable, printJson, object);
				}
			}

			@Override
			public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
				JsonElement jsonElement = ops.convertTo(JSON_OPS, input);
				T output = deserialize.apply(jsonElement);
				return DataResult.success(Pair.of(output, input));
			}
		};
	}

	private static @Nullable <E> E returnNullThr(@NotNull Class<?> type, @NotNull Throwable throwable, boolean printJson, JsonElement jsonElement) {
		log.error("Unable to build datatype '{}' because: [ {} ]", type.getSimpleName(), throwable.getClass().getSimpleName() + " :: " + throwable.getMessage());
		if (printJson) {
			log.error("JsonElement : {}", jsonElement.toString());
		}
		return null;
	}

	static <A> @NotNull Codec<A> of(final Encoder<A> encoder, final Decoder<A> decoder, final String name, Class<?> type) {
		return new AbstractSerializableDataType<>(type) {
			@Override
			public A deserialize(JsonElement object) {
				if (object == null) return null;
				return decoder.decode(JSON_OPS, object).getOrThrow().getFirst();
			}

			@Override
			public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input) {
				return decoder.decode(ops, input);
			}

			@Override
			public <T> DataResult<T> encode(final A input, final DynamicOps<T> ops, final T prefix) {
				return encoder.encode(input, ops, prefix);
			}

			@Override
			public String toString() {
				return name;
			}
		};
	}

	static @Nullable SerializableData.Instance strictCompound(@NotNull SerializableData definer, JsonObject object, @NotNull Class<?> classType) {
		HashMap<String, Object> backend = new LinkedHashMap<>();
		AtomicBoolean failed = new AtomicBoolean(false);

		definer.dataMap().forEach((key, tiedState) -> {
			if (failed.get()) return;
			SerializableType state = (SerializableType) tiedState.state();
			switch (state) {
				case DEFAULT -> {
					if (object.has(key)) {
						JsonElement element = object.get(key);
						backend.put(key, tiedState.object().deserialize(element));
					} else {
						backend.put(key, definer.defaultMap.get(key));
					}
				}
				case REQUIRED -> {
					if (!object.has(key)) {
						LOGGER.error("JsonObject for type [{}] is missing required key '{}'", classType.getSimpleName(), key);
						failed.set(true);
						return;
					}

					JsonElement element = object.get(key);
					backend.put(key, tiedState.object().deserialize(element));
				}
				case null, default -> {
				}
			}
		});

		if (failed.get()) {
			return null;
		}

		SerializableData.Instance instance = new SerializableData.Instance(backend);
		if (definer.postProcessor != null) {
			definer.postProcessor.accept(instance);
		}
		return instance;
	}

	static @NotNull <T> SerializableDataType<T> strictCompound(SerializableData definer, Function<SerializableData.Instance, T> fromData, Class<?> classType) {
		return SerializableDataType.of(
			(jsonElement) -> {
				if (!(jsonElement instanceof JsonObject jo)) throw new JsonSyntaxException("Expected JsonObject");
				SerializableData.Instance compound = strictCompound(definer, jo, classType);
				return fromData.apply(compound);
			}, classType
		);
	}

	T deserialize(JsonElement object);

	Class<?> type();

	<S> Codec<S> comapFlatMap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends T> from, Class<?> type);

	default String asString() {
		return type().getSimpleName();
	}

	abstract class AbstractSerializableDataType<T> implements SerializableDataType<T> {
		private final Class<?> type;

		protected AbstractSerializableDataType(Class<?> type) {
			this.type = type;
		}

		@Override
		public Class<?> type() {
			return type;
		}

		@Override
		public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
			return null;
		}

		@Override
		public <S> Codec<S> comapFlatMap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends T> from, Class<?> type) {
			return SerializableDataType.of(comap(from), flatMap(to), this + "[comapFlatMapped]", type);
		}
	}
}
