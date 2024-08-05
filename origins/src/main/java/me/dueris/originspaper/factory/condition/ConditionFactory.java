package me.dueris.originspaper.factory.condition;

import com.google.gson.JsonObject;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.factory.Factory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ConditionFactory<T> implements Factory, Predicate<T> {
	protected final BiPredicate<DeserializedFactoryJson, T> effect;
	protected final InstanceDefiner data;
	private final ResourceLocation location;
	public DeserializedFactoryJson deserializedFactory = null;

	public ConditionFactory(ResourceLocation location, @NotNull InstanceDefiner data, @NotNull BiPredicate<DeserializedFactoryJson, T> effect) {
		this.location = location;
		this.data = data.add("inverted", SerializableDataTypes.BOOLEAN, false);
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
		this.deserializedFactory = DeserializedFactoryJson.decompileJsonObject(object, data, "Condition Factory", location.toString());
		return this;
	}
}
