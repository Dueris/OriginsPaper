package io.github.dueris.calio.data;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class RecursiveSerializableDataType<T> extends SerializableDataType<T> {

	private final Supplier<SerializableDataType<T>> dataType;
	private final Function<SerializableDataType<T>, SerializableDataType<T>> wrapper;

	public RecursiveSerializableDataType(Function<SerializableDataType<T>, SerializableDataType<T>> wrapper, Optional<String> name, boolean root) {
		super(null, name, root);
		this.dataType = Suppliers.memoize(() -> wrapper.apply(this));
		this.wrapper = wrapper;
	}

	public RecursiveSerializableDataType(Function<SerializableDataType<T>, SerializableDataType<T>> wrapper) {
		this(wrapper, Optional.empty(), true);
	}

	@Override
	public Codec<T> codec() {
		return this.dataType.get().codec();
	}

	@Override
	public <S> RecursiveSerializableDataType<S> xmap(Function<? super T, ? extends S> to, Function<? super S, ? extends T> from) {
		return new RecursiveSerializableDataType<>(dt -> wrapper.apply(this).xmap(to, from), this.getName(), this.isRoot());
	}

	@Override
	public <S> RecursiveSerializableDataType<S> comapFlatMap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends T> from) {
		return new RecursiveSerializableDataType<>(dt -> wrapper.apply(this).comapFlatMap(to, from), this.getName(), this.isRoot());
	}

	@Override
	public <S> RecursiveSerializableDataType<S> flatComapMap(Function<? super T, ? extends S> to, Function<? super S, ? extends DataResult<? extends T>> from) {
		return new RecursiveSerializableDataType<>(dt -> wrapper.apply(this).flatComapMap(to, from), this.getName(), this.isRoot());
	}

	@Override
	public <S> RecursiveSerializableDataType<S> flatXmap(Function<? super T, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends T>> from) {
		return new RecursiveSerializableDataType<>(dt -> wrapper.apply(this).flatXmap(to, from), this.getName(), this.isRoot());
	}

	@Override
	public RecursiveSerializableDataType<T> setRoot(boolean root) {
		return new RecursiveSerializableDataType<>(dt -> wrapper.apply(dt).setRoot(root), this.getName(), root);
	}

	@SuppressWarnings("unchecked")
	public <DT extends SerializableDataType<T>> DT cast() {
		return (DT) dataType.get();
	}

}

