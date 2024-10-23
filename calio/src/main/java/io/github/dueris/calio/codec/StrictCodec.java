package io.github.dueris.calio.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public interface StrictCodec<A> extends Codec<A>, StrictEncoder<A>, StrictDecoder<A> {

	static <A> StrictCodec<A> of(Codec<A> codec) {
		return new StrictCodec<>() {

			@Override
			public <T> T strictEncode(A input, DynamicOps<T> ops, T prefix) {
				return codec.encode(input, ops, prefix).getOrThrow();
			}

			@Override
			public <T> Pair<A, T> strictDecode(DynamicOps<T> ops, T input) {
				return codec.decode(ops, input).getOrThrow();
			}

		};
	}

	static <A> StrictCodec<A> of(final StrictEncoder<A> encoder, final StrictDecoder<A> decoder) {
		return of(encoder, decoder, "StrictCodec[" + encoder + " " + decoder + "]");
	}

	static <A> StrictCodec<A> of(final StrictEncoder<A> encoder, final StrictDecoder<A> decoder, final String name) {
		return new StrictCodec<>() {

			@Override
			public <T> T strictEncode(A input, DynamicOps<T> ops, T prefix) {
				return encoder.strictEncode(input, ops, prefix);
			}

			@Override
			public <T> Pair<A, T> strictDecode(DynamicOps<T> ops, T input) {
				return decoder.strictDecode(ops, input);
			}

			@Override
			public String toString() {
				return name;
			}

		};
	}

	static <F, S> StrictCodec<Pair<F, S>> pair(final StrictCodec<F> firstCodec, final StrictCodec<S> secondCodec) {
		return new StrictCodec<>() {

			@Override
			public <T> Pair<Pair<F, S>, T> strictDecode(DynamicOps<T> ops, T input) {

				Pair<F, T> firstResult = firstCodec.strictDecode(ops, input);
				Pair<S, T> secondResult = secondCodec.strictDecode(ops, firstResult.getSecond());

				return Pair.of(Pair.of(firstResult.getFirst(), secondResult.getFirst()), secondResult.getSecond());

			}

			@Override
			public <T> T strictEncode(Pair<F, S> input, DynamicOps<T> ops, T prefix) {
				T secondResult = secondCodec.strictEncode(input.getSecond(), ops, prefix);
				return firstCodec.strictEncode(input.getFirst(), ops, secondResult);
			}

		};
	}

	static <F, S> StrictCodec<Either<F, S>> either(final StrictCodec<F> firstCodec, final StrictCodec<S> secondCodec) {
		return new StrictCodec<>() {

			@Override
			public <T> Pair<Either<F, S>, T> strictDecode(DynamicOps<T> ops, T input) {

				Pair<Either<F, S>, T> firstResult;
				Exception firstException;

				try {
					firstResult = firstCodec.strictDecode(ops, input).mapFirst(Either::left);
					return firstResult;
				} catch (Exception e) {
					firstException = e;
				}

				Pair<Either<F, S>, T> secondResult;
				Exception secondException;

				try {
					secondResult = secondCodec.strictDecode(ops, input).mapFirst(Either::right);
					return secondResult;
				} catch (Exception e) {
					secondException = e;
				}

				throw new IllegalStateException("Failed to parse either. First: " + firstException.getMessage() + ". Second: " + secondException.getMessage());

			}

			@Override
			public <T> T strictEncode(Either<F, S> input, DynamicOps<T> ops, T prefix) {
				return input.map(
					first -> firstCodec.strictEncode(first, ops, prefix),
					second -> secondCodec.strictEncode(second, ops, prefix)
				);
			}

		};
	}

	static <P> StrictCodec<P> withAlternative(final StrictCodec<P> primary, final StrictCodec<? extends P> alternative) {
		return either(
			primary,
			alternative
		).xmap(
			Either::unwrap,
			Either::left
		);
	}

	static <P, S> StrictCodec<P> withAlternative(final StrictCodec<P> primary, final StrictCodec<S> alternative, final Function<S, P> converter) {
		return either(
			primary,
			alternative
		).xmap(
			either -> either.map(primaryValue -> primaryValue, converter),
			Either::left
		);
	}

	static <F> StrictMapCodec<Optional<F>> optionalField(final String name, final StrictCodec<F> elementCodec, final boolean lenient) {
		return new StrictOptionalFieldCodec<>(name, elementCodec, lenient);
	}

	@Override
	default <S> StrictCodec<S> xmap(Function<? super A, ? extends S> to, Function<? super S, ? extends A> from) {
		return of(comap(from), map(to), this + "[xmapped]");
	}

	@Override
	default <S> StrictCodec<S> comapFlatMap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends A> from) {
		return of(comap(from), flatMap(to), this + "[comapFlatMapped]");
	}

	@Override
	default <S> StrictCodec<S> flatComapMap(Function<? super A, ? extends S> to, Function<? super S, ? extends DataResult<? extends A>> from) {
		return of(flatComap(from), map(to), this + "[flatComapMapped]");
	}

	@Override
	default <S> StrictCodec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends A>> from) {
		return of(flatComap(from), flatMap(to), this + "[flatXmapped]");
	}

	@Override
	default StrictMapCodec<A> fieldOf(String name) {
		return StrictMapCodec.of(
			StrictEncoder.super.fieldOf(name),
			StrictDecoder.super.fieldOf(name)
		);
	}

	@Override
	default StrictMapCodec<Optional<A>> optionalFieldOf(String name) {
		return optionalField(name, this, false);
	}

	@Override
	default StrictMapCodec<A> optionalFieldOf(String name, A defaultValue) {
		return optionalField(name, this, false).xmap(
			o -> o.orElse(defaultValue),
			a -> Objects.equals(a, defaultValue)
				? Optional.empty()
				: Optional.of(a)
		);
	}

	@Override
	default StrictMapCodec<Optional<A>> lenientOptionalFieldOf(String name) {
		return optionalField(name, this, false);
	}

	@Override
	default StrictMapCodec<A> lenientOptionalFieldOf(String name, A defaultValue) {
		return optionalField(name, this, true).xmap(
			o -> o.orElse(defaultValue),
			a -> Objects.equals(a, defaultValue)
				? Optional.empty()
				: Optional.of(a)
		);
	}

}
