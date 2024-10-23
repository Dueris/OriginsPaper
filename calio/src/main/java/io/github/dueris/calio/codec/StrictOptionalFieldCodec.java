package io.github.dueris.calio.codec;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class StrictOptionalFieldCodec<A> extends StrictMapCodec<Optional<A>> {

	private final String name;
	private final StrictCodec<A> elementCodec;

	private final boolean lenient;

	public StrictOptionalFieldCodec(String name, StrictCodec<A> elementCodec, boolean lenient) {
		this.name = name;
		this.elementCodec = elementCodec;
		this.lenient = lenient;
	}

	@Override
	public <T> Optional<A> strictDecode(DynamicOps<T> ops, MapLike<T> input) {

		T value = input.get(name);
		if (value == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(elementCodec.strictParse(ops, value));
		} catch (Exception e) {

			if (lenient) {
				return Optional.empty();
			} else {
				throw e;
			}

		}

	}

	@Override
	public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
		return input
			.map(a -> prefix.add(name, elementCodec.strictEncodeStart(ops, a)))
			.orElse(prefix);
	}

	@Override
	public <T> Stream<T> keys(DynamicOps<T> ops) {
		return Stream.of(ops.createString(name));
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		} else if (obj instanceof StrictOptionalFieldCodec<?> that) {
			return this.name.equals(that.name)
				&& this.elementCodec.equals(that.elementCodec)
				&& this.lenient == that.lenient;
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(name, elementCodec, lenient);
	}

	@Override
	public String toString() {
		return "StrictOptionalFieldCodec[" + name + ": " + elementCodec + "]";
	}

}
