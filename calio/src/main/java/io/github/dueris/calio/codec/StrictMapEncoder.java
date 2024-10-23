package io.github.dueris.calio.codec;

import com.mojang.serialization.*;

import java.util.function.Function;
import java.util.stream.Stream;

public interface StrictMapEncoder<A> extends MapEncoder<A> {

	<T> T strictEncode(A input, DynamicOps<T> ops, T prefix);

	@Override
	default <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

		try {

			ops.getMapEntries(this.strictEncode(input, ops, ops.empty()))
				.getOrThrow()
				.accept(prefix::add);

			return prefix;

		} catch (Exception e) {
			return prefix;
		}

	}

	@Override
	default <B> StrictMapEncoder<B> comap(Function<? super B, ? extends A> function) {
		return new StrictMapEncoder.Implementation<>() {

			@Override
			public <T> T strictEncode(B input, DynamicOps<T> ops, T prefix) {
				return StrictMapEncoder.this.strictEncode(function.apply(input), ops, prefix);
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return StrictMapEncoder.this.keys(ops);
			}

			@Override
			public String toString() {
				return StrictMapEncoder.this + "[comapped]";
			}

		};
	}

	@Override
	default <B> StrictMapEncoder<B> flatComap(Function<? super B, ? extends DataResult<? extends A>> function) {
		return new StrictMapEncoder.Implementation<>() {

			@Override
			public <T> T strictEncode(B input, DynamicOps<T> ops, T prefix) {
				return function.apply(input)
					.map(r -> StrictMapEncoder.this.strictEncode(r, ops, prefix))
					.getOrThrow();
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return StrictMapEncoder.this.keys(ops);
			}

			@Override
			public String toString() {
				return StrictMapEncoder.this + "[flatComapped]";
			}

		};
	}

	@Override
	default StrictEncoder<A> encoder() {
		return new StrictEncoder<>() {

			@Override
			public <T> T strictEncode(A input, DynamicOps<T> ops, T prefix) {
				return StrictMapEncoder.this.strictEncode(input, ops, prefix);
			}

			@Override
			public String toString() {
				return StrictMapEncoder.this.toString();
			}

		};
	}

	abstract class Implementation<A> extends CompressorHolder implements StrictMapEncoder<A> {

	}

}
