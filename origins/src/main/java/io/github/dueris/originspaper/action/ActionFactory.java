package io.github.dueris.originspaper.action;

import com.google.gson.JsonObject;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.Factory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ActionFactory<T> implements Factory, Consumer<T> {
	protected final BiConsumer<SerializableData.Instance, T> effect;
	protected final SerializableData data;
	private final ResourceLocation location;
	public SerializableData.Instance deserializedFactory = null;

	public ActionFactory(ResourceLocation location, SerializableData data, @NotNull BiConsumer<SerializableData.Instance, T> effect) {
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

	public ActionFactory<T> copy() {
		return new ActionFactory<T>(location, data, effect);
	}

	public ActionFactory<T> decompile(JsonObject object) {
		this.deserializedFactory = SerializableData.Instance.decompileJsonObject(object, data, "Action Factory", location.toString(), Optional.of(this.getClass()));
		if (data.postProcessor != null) {
			data.postProcessor.accept(deserializedFactory);
		}
		return this;
	}
}
