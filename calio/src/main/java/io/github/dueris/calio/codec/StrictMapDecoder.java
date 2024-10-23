package io.github.dueris.calio.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StrictMapDecoder<A> extends MapDecoder<A> {

	<T> A strictDecode(DynamicOps<T> ops, MapLike<T> input);

	default <T> A compressedStrictDecode(final DynamicOps<T> ops, final T input) {

		if (!ops.compressMaps()) {
			return this.strictDecode(ops, ops.getMap(input).getOrThrow());
		} else {

			Consumer<Consumer<T>> inputs = ops
				.getList(input)
				.getOrThrow(err -> new IllegalArgumentException("Input is not a list"));

			KeyCompressor<T> compressor = this.compressor(ops);
			List<T> entries = new ArrayList<>();

			inputs.accept(entries::add);
			return this.strictDecode(ops, new MapLike<>() {

				@Override
				public T get(T key) {
					return entries.get(compressor.compress(key));
				}

				@Override
				public T get(String key) {
					return entries.get(compressor.compress(key));
				}

				@Override
				public Stream<Pair<T, T>> entries() {
					return IntStream.range(0, entries.size())
						.mapToObj(i -> Pair.of(compressor.decompress(i), entries.get(i)))
						.filter(p -> p.getSecond() != null);
				}

			});

		}

	}

	@Override
	default <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {

		try {
			return DataResult.success(this.strictDecode(ops, input));
		} catch (Exception e) {
			return DataResult.error(e::getMessage);
		}

	}

	@Override
	default <T> DataResult<A> compressedDecode(DynamicOps<T> ops, T input) {

		try {
			return DataResult.success(this.compressedStrictDecode(ops, input));
		} catch (Exception e) {
			return DataResult.error(e::getMessage);
		}

	}

	@Override
	default StrictDecoder<A> decoder() {
		return new StrictDecoder<>() {

			@Override
			public <T> Pair<A, T> strictDecode(DynamicOps<T> ops, T input) {
				return Pair.of(compressedStrictDecode(ops, input), input);
			}

			@Override
			public String toString() {
				return StrictMapDecoder.this.toString();
			}

		};
	}

	@Override
	default <B> StrictMapDecoder<B> flatMap(Function<? super A, ? extends DataResult<? extends B>> function) {
		return new StrictMapDecoder.Implementation<>() {

			@Override
			public <T> B strictDecode(DynamicOps<T> ops, MapLike<T> input) {
				return function.apply(StrictMapDecoder.this.strictDecode(ops, input)).getOrThrow();
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return StrictMapDecoder.this.keys(ops);
			}

			@Override
			public String toString() {
				return StrictMapDecoder.this + "[flatMapped]";
			}

		};
	}

	@Override
	default <B> StrictMapDecoder<B> map(Function<? super A, ? extends B> function) {
		return new StrictMapDecoder.Implementation<>() {

			@Override
			public <T> B strictDecode(DynamicOps<T> ops, MapLike<T> input) {
				return function.apply(StrictMapDecoder.this.strictDecode(ops, input));
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return StrictMapDecoder.this.keys(ops);
			}

			@Override
			public String toString() {
				return StrictMapDecoder.this + "[mapped]";
			}

		};
	}

	default <E> StrictMapDecoder<E> ap(StrictMapDecoder<Function<? super A, ? extends E>> decoder) {
		return new StrictMapDecoder.Implementation<>() {

			@Override
			public <T> E strictDecode(DynamicOps<T> ops, MapLike<T> input) {

				A a = StrictMapDecoder.this.strictDecode(ops, input);
				Function<? super A, ? extends E> function = decoder.strictDecode(ops, input);

				return function.apply(a);

			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.concat(StrictMapDecoder.this.keys(ops), decoder.keys(ops));
			}

			@Override
			public String toString() {
				return decoder + " * " + StrictMapDecoder.this;
			}

		};
	}

	abstract class Implementation<A> extends CompressorHolder implements StrictMapDecoder<A> {

	}

}
