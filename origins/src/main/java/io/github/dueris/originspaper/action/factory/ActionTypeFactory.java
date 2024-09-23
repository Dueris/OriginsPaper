package io.github.dueris.originspaper.action.factory;

import com.google.gson.JsonObject;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.Factory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ActionTypeFactory<T> implements Factory, Consumer<T> {
	protected final BiConsumer<SerializableData.Instance, T> effect;
	protected final SerializableData data;
	private final ResourceLocation location;
	public SerializableData.Instance deserializedFactory = null;

	public ActionTypeFactory(ResourceLocation location, SerializableData data, @NotNull BiConsumer<SerializableData.Instance, T> effect) {
		this.location = location;
		this.data = data;
		this.effect = effect;
	}

	@Override
	public ResourceLocation getSerializerId() {
		return location;
	}

	@Override
	public SerializableData getSerializableData() {
		return data;
	}

	@Override
	public void accept(T t) {
		if (deserializedFactory == null)
			throw new IllegalStateException("Unable to execute ActionFactory because there was no DeserializedFactoryJson compiled!");
		effect.accept(deserializedFactory, t);
	}

	public ActionTypeFactory<T> copy() {
		return new ActionTypeFactory<T>(location, data, effect);
	}

	public ActionTypeFactory<T> decompile(JsonObject object) {
		this.deserializedFactory = SerializableDataType.strictCompound(data, object, this.getClass());
		if (data.postProcessor != null) {
			data.postProcessor.accept(deserializedFactory);
		}
		return this;
	}
}
