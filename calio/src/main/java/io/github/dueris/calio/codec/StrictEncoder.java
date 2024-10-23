package io.github.dueris.calio.codec;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapEncoder;

import java.util.function.Function;

public interface StrictEncoder<A> extends Encoder<A> {

	<T> T strictEncode(final A input, final DynamicOps<T> ops, final T prefix);

	default <T> T strictEncodeStart(final DynamicOps<T> ops, final A input) {
		return strictEncode(input, ops, ops.empty());
	}

	@Override
	default <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {

		try {
			return DataResult.success(this.strictEncode(input, ops, prefix));
		} catch (Throwable t) {
			return DataResult.error(t::getMessage);
		}

	}

	@Override
	default MapEncoder<A> fieldOf(String name) {
		return Encoder.super.fieldOf(name);
	}

	@Override
	default <B> StrictEncoder<B> comap(Function<? super B, ? extends A> function) {
		return new StrictEncoder<>() {

			@Override
			public <T> T strictEncode(B input, DynamicOps<T> ops, T prefix) {
				return StrictEncoder.this.strictEncode(function.apply(input), ops, prefix);
			}

			@Override
			public String toString() {
				return StrictEncoder.this + "[comapped]";
			}

		};
	}

	@Override
	default <B> StrictEncoder<B> flatComap(Function<? super B, ? extends DataResult<? extends A>> function) {
		return new StrictEncoder<>() {

			@Override
			public <T> T strictEncode(B input, DynamicOps<T> ops, T prefix) {
				return function.apply(input).map(a -> StrictEncoder.this.strictEncode(a, ops, prefix)).getOrThrow();
			}

			@Override
			public String toString() {
				return StrictEncoder.this + "[flatComapped]";
			}

		};
	}

}
