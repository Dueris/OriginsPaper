package io.github.dueris.calio.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import io.github.dueris.calio.data.exceptions.DataException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class SerializableDataTypeList<E> extends SerializableDataType<List<E>> {

	public SerializableDataTypeList(CustomCodec<E> listCodec, Optional<String> name, boolean root) {
		super(listCodec, name, root);
	}

	public SerializableDataTypeList(CustomCodec<E> listCodec) {
		this(listCodec, Optional.empty(), true);
	}

	@Override
	public CustomCodec<E> codec() {
		return (CustomCodec<E>) super.codec();
	}

	@Override
	public <S> SerializableDataType<S> xmap(Function<? super List<E>, ? extends S> to, Function<? super S, ? extends List<E>> from) {
		SerializableDataTypeList<E> self = this;
		return recursive(dataType -> new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T input) {
					return self.setRoot(dataType.isRoot()).codec().decode(ops, input)
						.map(listAndInput -> listAndInput
							.mapFirst(to));
				}

				@Override
				public <T> DataResult<T> encode(S input, DynamicOps<T> ops, T prefix) {
					return self.setRoot(dataType.isRoot()).codec().encode(from.apply(input), ops, prefix);
				}

			},
			dataType.getName(),
			dataType.isRoot()
		));
	}

	@Override
	public <S> SerializableDataType<S> comapFlatMap(Function<? super List<E>, ? extends DataResult<? extends S>> to, Function<? super S, ? extends List<E>> from) {
		SerializableDataTypeList<E> self = this;
		return recursive(dataType -> new SerializableDataType<>(
			new Codec<>() {

				@SuppressWarnings("unchecked")
				@Override
				public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T input) {
					return self.setRoot(dataType.isRoot()).codec().parse(ops, input)
						.flatMap(list -> (DataResult<S>) to.apply(list))
						.map(s -> Pair.of(s, input));
				}

				@Override
				public <T> DataResult<T> encode(S input, DynamicOps<T> ops, T prefix) {
					return self.setRoot(dataType.isRoot()).codec().encode(from.apply(input), ops, prefix);
				}

			},
			dataType.getName(),
			dataType.isRoot()
		));
	}

	@Override
	public <S> SerializableDataType<S> flatComapMap(Function<? super List<E>, ? extends S> to, Function<? super S, ? extends DataResult<? extends List<E>>> from) {
		SerializableDataTypeList<E> self = this;
		return recursive(dataType -> new SerializableDataType<>(
			new Codec<>() {

				@Override
				public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T input) {
					return self.setRoot(dataType.isRoot()).codec().decode(ops, input)
						.map(listAndInput -> listAndInput
							.mapFirst(to));
				}

				@Override
				public <T> DataResult<T> encode(S input, DynamicOps<T> ops, T prefix) {
					return from.apply(input)
						.flatMap(list -> self.setRoot(dataType.isRoot()).codec().encode(list, ops, prefix));
				}

			},
			dataType.getName(),
			dataType.isRoot()
		));
	}

	@Override
	public <S> SerializableDataType<S> flatXmap(Function<? super List<E>, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends List<E>>> from) {
		SerializableDataTypeList<E> self = this;
		return recursive(dataType -> new SerializableDataType<>(
			new Codec<>() {

				@SuppressWarnings("unchecked")
				@Override
				public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T input) {
					return self.setRoot(dataType.isRoot()).codec().parse(ops, input)
						.flatMap(list -> (DataResult<S>) to.apply(list))
						.map(s -> Pair.of(s, input));
				}

				@Override
				public <T> DataResult<T> encode(S input, DynamicOps<T> ops, T prefix) {
					return from.apply(input)
						.flatMap(list -> self.setRoot(dataType.isRoot()).codec().encode(list, ops, prefix));
				}

			},
			dataType.getName(),
			dataType.isRoot()
		));
	}

	@Override
	public SerializableDataTypeList<E> setRoot(boolean root) {

		SerializableDataType<E> dataType = this.codec().dataType().setRoot(root);

		int minSize = this.codec().minSize();
		int maxSize = this.codec().maxSize();

		return new SerializableDataTypeList<>(new CustomCodec<>(dataType, minSize, maxSize));

	}

	public static class CustomCodec<E> implements Codec<List<E>> {

		private final SerializableDataType<E> dataType;

		private final int minSize;
		private final int maxSize;

		CustomCodec(SerializableDataType<E> dataType, int minSize, int maxSize) {
			this.dataType = dataType;
			this.minSize = minSize;
			this.maxSize = maxSize;
		}

		@Override
		public <T> DataResult<Pair<List<E>, T>> decode(DynamicOps<T> ops, T input) {

			DataResult<Consumer<Consumer<T>>> listInputResult = ops.getList(input);
			if (listInputResult.isSuccess()) {

				try {

					Stream.Builder<T> inputsBuilder = Stream.builder();
					List<E> elements = new ObjectArrayList<>();

					return listInputResult.flatMap(list -> {

						AtomicInteger currentIndex = new AtomicInteger(0);
						try {

							list.accept(elementInput -> {

								if (elements.size() <= maxSize) {

									E element = dataType.codec()
										.parse(ops, elementInput)
										.getOrThrow();

									elements.add(element);
									inputsBuilder.add(elementInput);

									currentIndex.addAndGet(1);

								}

							});

							if (elements.size() > maxSize) {
								return createTooLongError(elements.size());
							} else if (elements.size() < minSize) {
								return createTooShortError(elements.size());
							} else {

								T inputs = ops.createList(inputsBuilder.build());
								List<E> immutableElements = List.copyOf(elements);

								return DataResult.success(Pair.of(immutableElements, inputs));

							}

						} catch (DataException de) {
							throw de.prependArray(currentIndex.get());
						} catch (Exception e) {
							throw new DataException(DataException.Phase.READING, currentIndex.get(), e);
						}

					});

				} catch (Exception e) {

					if (dataType().isRoot()) {
						return DataResult.error(e::getMessage);
					} else {
						throw e;
					}

				}

			} else {
				return dataType().codec().decode(ops, input)
					.map(elementAndInput -> elementAndInput
						.mapFirst(List::of));
			}

		}

		@Override
		public <T> DataResult<T> encode(List<E> listInput, DynamicOps<T> ops, T prefix) {

			ListBuilder<T> listBuilder = ops.listBuilder();
			int size = listInput.size();

			if (size > maxSize) {
				return createTooLongError(size);
			} else if (size < minSize) {
				return createTooShortError(size);
			} else {

				try {

					int index = 0;
					for (E element : listInput) {

						try {

							listBuilder.add(dataType.codec()
								.encodeStart(ops, element)
								.getOrThrow());

							index++;

						} catch (DataException de) {
							throw de.prependArray(index);
						} catch (Exception e) {
							throw new DataException(DataException.Phase.WRITING, index, e);
						}

					}

					return listBuilder.build(prefix);

				} catch (Exception e) {

					if (dataType().isRoot()) {
						return DataResult.error(e::getMessage);
					} else {
						throw e;
					}

				}

			}

		}

		public SerializableDataType<E> dataType() {
			return dataType;
		}

		public int minSize() {
			return minSize;
		}

		public int maxSize() {
			return maxSize;
		}

		private <R> DataResult<R> createTooLongError(int size) {
			return DataResult.error(() -> "Expected collection to have at most " + maxSize + " element(s); found " + size + " element(s)!");
		}

		private <R> DataResult<R> createTooShortError(int size) {
			return DataResult.error(() -> "Expected collection to have at least " + minSize + " element(s); found only " + size + " element(s)!");
		}

	}

}

