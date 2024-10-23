package io.github.dueris.originspaper.action.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.Factory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActionTypeFactory<T> implements Factory {

	protected final ResourceLocation id;

	protected final SerializableData serializableData;
	protected final Function<SerializableData.Instance, Consumer<T>> effectFactory;

	public ActionTypeFactory(ResourceLocation id, SerializableData serializableData, @NotNull BiConsumer<SerializableData.Instance, T> effect) {
		this.id = id;
		this.serializableData = serializableData.copy();
		this.effectFactory = data -> t -> effect.accept(data, t);
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
	public ActionTypeFactory.Instance fromData(SerializableData.Instance data) {
		return new ActionTypeFactory.Instance(data);
	}

	public class Instance implements Factory.Instance, Consumer<T> {

		protected final SerializableData.Instance data;
		protected final Consumer<T> effect;

		protected Instance(SerializableData.Instance data) {
			this.effect = effectFactory.apply(data);
			this.data = data;
		}

		@Override
		public void accept(T t) {
			effect.accept(t);
		}

		@Override
		public SerializableData.Instance getData() {
			return data;
		}

		@Override
		public ActionTypeFactory<T> getFactory() {
			return ActionTypeFactory.this;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			} else if (obj instanceof ActionTypeFactory<?>.Instance other) {
				return this.getData().equals(other.getData())
					&& this.getFactory().equals(other.getFactory());
			} else {
				return false;
			}

		}

	}

}
