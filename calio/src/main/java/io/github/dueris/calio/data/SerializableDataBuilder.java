package io.github.dueris.calio.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.dueris.calio.parser.SerializableData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface SerializableDataBuilder<T> extends Codec<T> {
	DynamicOps<JsonElement> JSON_OPS = JsonOps.INSTANCE;

	static <T> @NotNull SerializableDataBuilder<List<T>> of(Codec<List<T>> listCodec) {
		return of(listCodec, List.class);
	}

	static <T> @NotNull SerializableDataBuilder<T> of(Codec<T> codec, Class<?> type) {
		return new AbstractSerializableDataBuilder<T>(type) {
			@Override
			public T deserialize(JsonElement object) {
				return decode(JSON_OPS, object).getOrThrow().getFirst();
			}

			@Override
			public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
				return codec.decode(ops, input);
			}
		};
	}

	static <T> @NotNull SerializableDataBuilder<T> of(Function<JsonElement, T> deserialize, Class<?> type) {
		return new AbstractSerializableDataBuilder<T>(type) {
			@Override
			public T deserialize(JsonElement object) {
				return deserialize.apply(object);
			}

			@Override
			public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
				JsonElement jsonElement = ops.convertTo(JSON_OPS, input);
				T output = deserialize.apply(jsonElement);
				return DataResult.success(Pair.of(output, input));
			}
		};
	}

	static <A> @NotNull Codec<A> of(final Encoder<A> encoder, final Decoder<A> decoder, final String name, Class<?> type) {
		return new AbstractSerializableDataBuilder<A>(type) {
			@Override
			public A deserialize(JsonElement object) {
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

	static @NotNull SerializableData.Instance compound(SerializableData definer, JsonObject object, @NotNull Class<?> classType) {
		return SerializableData.Instance
			.decompileJsonObject(object, definer, "null", classType.getSimpleName(), Optional.of(classType));
	}

	T deserialize(JsonElement object);

	Class<?> type();

	<S> Codec<S> comapFlatMap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends T> from, Class<?> type);

	default String asString() {
		return type().getSimpleName();
	}

	abstract class AbstractSerializableDataBuilder<T> implements SerializableDataBuilder<T> {
		private final Class<?> type;

		protected AbstractSerializableDataBuilder(Class<?> type) {
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
			return SerializableDataBuilder.of(comap(from), flatMap(to), this + "[comapFlatMapped]", type);
		}
	}
}
