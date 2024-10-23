package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.Factory;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConditionTypeFactory<T> implements Factory {

	protected final ResourceLocation id;

	protected final SerializableData serializableData;
	protected final Function<SerializableData.Instance, Predicate<T>> conditionFactory;

	public ConditionTypeFactory(ResourceLocation id, SerializableData serializableData, BiFunction<SerializableData.Instance, T, Boolean> condition) {
		this.id = id;
		this.serializableData = serializableData.copy().add("inverted", SerializableDataTypes.BOOLEAN, false);
		this.conditionFactory = data -> t -> condition.apply(data, t);
	}

	@Override
	public ResourceLocation getSerializerId() {
		return id;
	}

	@Override
	public SerializableData getSerializableData() {
		return serializableData;
	}

	@Override
	public ConditionTypeFactory.Instance fromData(SerializableData.Instance data) {
		return new ConditionTypeFactory.Instance(data);
	}

	public class Instance implements Factory.Instance, Predicate<T> {

		protected final SerializableData.Instance data;
		protected final Predicate<T> condition;

		protected Instance(SerializableData.Instance data) {
			this.condition = conditionFactory.apply(data);
			this.data = data;
		}

		@Override
		public SerializableData.Instance getData() {
			return data;
		}

		@Override
		public ConditionTypeFactory<T> getFactory() {
			return ConditionTypeFactory.this;
		}

		@Override
		public boolean test(T t) {
			return data.getBoolean("inverted") != condition.test(t);
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			} else if (obj instanceof ConditionTypeFactory<?>.Instance other) {
				return this.getData().equals(other.getData())
					&& this.getFactory().equals(other.getFactory());
			} else {
				return false;
			}

		}

	}

}
