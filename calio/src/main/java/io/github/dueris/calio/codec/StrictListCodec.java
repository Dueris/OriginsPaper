package io.github.dueris.calio.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import io.github.dueris.calio.data.exceptions.DataException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record StrictListCodec<E>(StrictCodec<E> elementCodec, int minSize,
								 int maxSize) implements StrictCodec<List<E>> {

	public static <E> @NotNull StrictListCodec<E> of(StrictCodec<E> elementCodec) {
		return of(elementCodec, Integer.MAX_VALUE);
	}

	public static <E> @NotNull StrictListCodec<E> of(StrictCodec<E> elementCodec, int maxSize) {
		return new StrictListCodec<>(elementCodec, 0, maxSize);
	}

	@Override
	public <T> T strictEncode(@NotNull List<E> input, @NotNull DynamicOps<T> ops, T prefix) {

		ListBuilder<T> listBuilder = ops.listBuilder();
		int size = input.size();

		if (size < minSize) {
			throw createTooShortError(size);
		} else if (size >= maxSize) {
			throw createTooLongError(size);
		}

		int index = 0;
		for (E element : input) {

			String path = "[" + index + "]";
			try {

				T encodedElement = elementCodec().strictEncodeStart(ops, element);
				listBuilder.add(encodedElement);

				index++;

			} catch (DataException de) {
				throw de.prepend(path);
			} catch (Exception e) {
				throw new DataException(DataException.Phase.WRITING, path, e);
			}

		}

		return listBuilder
			.build(prefix)
			.getOrThrow();

	}

	@Override
	public <T> @NotNull Pair<List<E>, T> strictDecode(@NotNull DynamicOps<T> ops, T input) {

		Consumer<Consumer<T>> inputs = ops
			.getList(input)
			.getOrThrow();

		DecoderState<T> decoder = new DecoderState<>(ops);
		inputs.accept(decoder::accept);

		return decoder.build();

	}

	@Override
	public @NotNull String toString() {
		return "StrictListCodec[" + elementCodec + "]";
	}

	private @NotNull RuntimeException createTooShortError(int size) {
		return new IllegalStateException("List has too few elements; expected to have at least " + minSize + " element(s), but only has " + size + " element(s)");
	}

	private @NotNull RuntimeException createTooLongError(int size) {
		return new IllegalStateException("List has too much elements; expected to have at most " + maxSize + " element(s), but has " + size + " element(s)");
	}

	private class DecoderState<T> {

		private final DynamicOps<T> ops;

		private final Stream.Builder<T> inputs;
		private final List<E> elements;

		private DecoderState(DynamicOps<T> ops) {
			this.ops = ops;
			this.inputs = Stream.builder();
			this.elements = new ObjectArrayList<>();
		}

		public void accept(T input) {

			int size = elements.size();
			String path = "[" + size + "]";

			if (size >= maxSize) {
				throw createTooLongError(elements.size());
			} else {

				try {
					elements.add(elementCodec().strictParse(ops, input));
					inputs.add(input);
				} catch (DataException de) {
					throw de.prepend(path);
				} catch (Exception e) {
					throw new DataException(DataException.Phase.READING, path, e);
				}

			}

		}

		public Pair<List<E>, T> build() {

			int size = elements.size();
			if (size < minSize) {
				throw createTooShortError(size);
			} else {

				if (size > maxSize) {
					throw createTooLongError(size);
				} else {
					return Pair.of(List.copyOf(elements), ops.createList(inputs.build()));
				}

			}

		}

	}

}
