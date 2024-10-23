package io.github.dueris.calio.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

public interface StrictDecoder<A> extends Decoder<A> {

	<T> Pair<A, T> strictDecode(final DynamicOps<T> ops, final T input);

	default <T> A strictParse(final DynamicOps<T> ops, final T input) {
		return this.strictDecode(ops, input).getFirst();
	}

	@Override
	default <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {

		try {
			return DataResult.success(this.strictDecode(ops, input));
		} catch (Throwable t) {
			return DataResult.error(t::getMessage);
		}

	}

	@Override
	default StrictMapDecoder<A> fieldOf(String name) {
		return new StrictFieldDecoder<>(name, this);
	}

	@Override
	default <B> StrictDecoder<B> flatMap(Function<? super A, ? extends DataResult<? extends B>> function) {
		return new StrictDecoder<>() {

			@Override
			public <T> Pair<B, T> strictDecode(DynamicOps<T> ops, T input) {
				Pair<A, T> a = StrictDecoder.this.strictDecode(ops, input);
				return function.apply(a.getFirst())
					.map(r -> Pair.of((B) r, a.getSecond()))
					.getOrThrow();
			}

			@Override
			public String toString() {
				return StrictDecoder.this + "[flatMapped]";
			}

		};
	}

	@Override
	default <B> StrictDecoder<B> map(Function<? super A, ? extends B> function) {
		return new StrictDecoder<>() {

			@Override
			public <T> Pair<B, T> strictDecode(DynamicOps<T> ops, T input) {
				return StrictDecoder.this.strictDecode(ops, input).mapFirst(function);
			}

			@Override
			public String toString() {
				return StrictDecoder.this + "[mapped]";
			}

		};
	}

}
