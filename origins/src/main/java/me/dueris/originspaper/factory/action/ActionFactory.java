package me.dueris.originspaper.factory.action;

import com.google.gson.JsonObject;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.factory.Factory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ActionFactory<T> implements Factory, Consumer<T> {
	protected final BiConsumer<DeserializedFactoryJson, T> effect;
	protected final InstanceDefiner data;
	private final ResourceLocation location;
	private DeserializedFactoryJson deserializedFactory = null;

	public ActionFactory(ResourceLocation location, InstanceDefiner data, @NotNull BiConsumer<DeserializedFactoryJson, T> effect) {
		this.location = location;
		this.data = data;
		this.effect = effect;
	}

	@Override
	public ResourceLocation getSerializerId() {
		return location;
	}

	@Override
	public InstanceDefiner getSerializableData() {
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
		this.deserializedFactory = DeserializedFactoryJson.decompileJsonObject(object, data, "Action Factory", location.toString());
		return this;
	}
}
