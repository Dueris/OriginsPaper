package io.github.dueris.originspaper.condition;

import com.google.gson.JsonObject;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.Factory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ConditionFactory<T> implements Factory, Predicate<T> {
	protected final BiPredicate<SerializableData.Instance, T> effect;
	protected final SerializableData data;
	private final ResourceLocation location;
	public SerializableData.Instance deserializedFactory = null;

	public ConditionFactory(ResourceLocation location, @NotNull SerializableData data, @NotNull BiPredicate<SerializableData.Instance, T> effect) {
		this.location = location;
		this.data = data.add("inverted", SerializableDataTypes.BOOLEAN, false);
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
	public final boolean test(T t) {
		if (deserializedFactory == null)
			throw new IllegalStateException("Unable to execute ActionFactory because there was no DeserializedFactoryJson compiled!");
		return deserializedFactory.getBoolean("inverted") != isFulfilled(t);
	}

	public boolean isFulfilled(T t) {
		return effect.test(deserializedFactory, t);
	}

	public ConditionFactory<T> copy() {
		return new ConditionFactory<T>(location, data, effect);
	}

	public ConditionFactory<T> decompile(JsonObject object) {
		this.deserializedFactory = SerializableData.Instance.decompileJsonObject(object, data, "Condition Factory", location.toString(), Optional.of(this.getClass()));
		return this;
	}
}
