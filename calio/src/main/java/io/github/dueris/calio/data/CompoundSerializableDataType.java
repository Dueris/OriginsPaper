package io.github.dueris.calio.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;

import java.util.Optional;
import java.util.function.Function;

public class CompoundSerializableDataType<T> extends SerializableDataType<T> {

	private final SerializableData serializableData;

	private final Function<SerializableData, MapCodec<T>> mapCodecGetter;

	public CompoundSerializableDataType(SerializableData serializableData, Function<SerializableData, MapCodec<T>> mapCodecGetter, Optional<String> name, boolean root) {
		super(null, name, root);
		this.serializableData = serializableData;
		this.mapCodecGetter = mapCodecGetter;
	}

	public CompoundSerializableDataType(SerializableData serializableData, Function<SerializableData, MapCodec<T>> mapCodecGetter) {
		this(serializableData, mapCodecGetter, Optional.empty(), true);
	}

	@Override
	public Codec<T> codec() {
		return mapCodec().codec();
	}

	@Override
	public <S> CompoundSerializableDataType<S> xmap(Function<? super T, ? extends S> to, Function<? super S, ? extends T> from) {
		return new CompoundSerializableDataType<>(
			serializableData(),
			_serializableData -> mapCodecGetter
				.apply(_serializableData)
				.xmap(to, from),
			this.getName(),
			this.isRoot()
		);
	}

	@Override
	public <S> CompoundSerializableDataType<S> comapFlatMap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends T> from) {

		Function<? super T, ? extends S> toUnwrapped = t -> to.apply(t).getOrThrow();
		Function<? super S, ? extends DataResult<? extends T>> fromWrapped = s -> DataResult.success(from.apply(s));

		return new CompoundSerializableDataType<>(
			serializableData(),
			_serializableData -> mapCodecGetter
				.apply(_serializableData)
				.flatXmap(to, fromWrapped),
			this.getName(),
			this.isRoot()
		);

	}

	@Override
	public <S> CompoundSerializableDataType<S> flatComapMap(Function<? super T, ? extends S> to, Function<? super S, ? extends DataResult<? extends T>> from) {

		Function<? super T, ? extends DataResult<? extends S>> toWrapped = t -> DataResult.success(to.apply(t));
		Function<? super S, ? extends T> fromUnwrapped = s -> from.apply(s).getOrThrow();

		return new CompoundSerializableDataType<>(
			serializableData(),
			_serializableData -> mapCodecGetter
				.apply(_serializableData)
				.flatXmap(toWrapped, from),
			this.getName(),
			this.isRoot()
		);

	}

	@Override
	public <S> CompoundSerializableDataType<S> flatXmap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends T>> from) {

		Function<? super T, ? extends S> toUnwrapped = t -> to.apply(t).getOrThrow();
		Function<? super S, ? extends T> fromUnwrapped = s -> from.apply(s).getOrThrow();

		return new CompoundSerializableDataType<>(
			serializableData(),
			_serializableData -> mapCodecGetter
				.apply(_serializableData)
				.flatXmap(to, from),
			this.getName(),
			this.isRoot()
		);

	}

	@Override
	public CompoundSerializableDataType<T> setRoot(boolean root) {
		return new CompoundSerializableDataType<>(serializableData().setRoot(root), this.mapCodecGetter, this.getName(), root);
	}

	public SerializableData serializableData() {
		return serializableData;
	}

	public MapCodec<T> mapCodec() {
		return mapCodecGetter.apply(serializableData());
	}

}
