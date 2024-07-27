package io.github.dueris.calio.util.holder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Pair<A, B>(A first, B second) {

	@Contract("_, _ -> new")
	public static <F, S> @NotNull Pair<F, S> of(F first, S second) {
		return new Pair<>(first, second);
	}

	@Contract("_ -> new")
	public static <F, S> @NotNull Pair<F, S> fromMojang(@Nullable com.mojang.datafixers.util.Pair<F, S> pair) {
		return new Pair<>(pair.getFirst(), pair.getSecond());
	}

	public A getA() {
		return first;
	}

	public B getB() {
		return second;
	}
}
