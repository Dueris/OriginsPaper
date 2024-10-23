package io.github.dueris.calio.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class StrictMapCodec<A> extends MapCodec<A> implements StrictMapDecoder<A> {

	public static <A> StrictMapCodec<A> of(final MapEncoder<A> encoder, final StrictMapDecoder<A> decoder) {
		return of(encoder, decoder, () -> "StrictMapCodec[" + encoder + " " + decoder + "]");
	}

	public static <A> StrictMapCodec<A> of(final MapEncoder<A> encoder, final StrictMapDecoder<A> decoder, final Supplier<String> name) {
		return new StrictMapCodec<>() {

			@Override
			public <T> A strictDecode(DynamicOps<T> ops, MapLike<T> input) {
				return decoder.strictDecode(ops, input);
			}

			@Override
			public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return encoder.encode(input, ops, prefix);
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.concat(encoder.keys(ops), decoder.keys(ops));
			}

			@Override
			public String toString() {
				return name.get();
			}

		};
	}

	@Override
	public StrictCodec<A> codec() {
		StrictMapCodec<A> thisMapCodec = StrictMapCodec.this;
		return new StrictCodec<>() {

			@Override
			public <T> Pair<A, T> strictDecode(DynamicOps<T> ops, T input) {
				return Pair.of(thisMapCodec.compressedStrictDecode(ops, input), input);
			}

			@Override
			public <T> T strictEncode(A input, DynamicOps<T> ops, T prefix) {
				return thisMapCodec.encode(input, ops, thisMapCodec.compressedBuilder(ops)).build(prefix).getOrThrow();
			}

			@Override
			public String toString() {
				return thisMapCodec.toString();
			}

		};
	}

	@Override
	public <S> StrictMapCodec<S> xmap(Function<? super A, ? extends S> to, Function<? super S, ? extends A> from) {
		return StrictMapCodec.of(comap(from), map(to), () -> this + "[xmapped]");
	}

	@Override
	public <S> StrictMapCodec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends A>> from) {
		return StrictMapCodec.of(flatComap(from), flatMap(to), () -> this + "[flatXmapped]");
	}

	@Override
	public StrictMapCodec<A> validate(Function<A, DataResult<A>> checker) {
		return flatXmap(checker, checker);
	}

}
