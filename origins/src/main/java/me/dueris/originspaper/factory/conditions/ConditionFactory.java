package me.dueris.originspaper.factory.conditions;

import com.google.gson.JsonObject;
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
	private DeserializedFactoryJson deserializedFactory = null;

	public ConditionFactory(ResourceLocation location, InstanceDefiner data, @NotNull BiPredicate<DeserializedFactoryJson, T> effect) {
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
	public boolean test(T t) {
		if (deserializedFactory == null)
			throw new IllegalStateException("Unable to execute ActionFactory because there was no DeserializedFactoryJson compiled!");
		return effect.test(deserializedFactory, t);
	}

	public ConditionFactory<T> copy() {
		return new ConditionFactory<T>(location, data, effect);
	}

	public ConditionFactory<T> decompile(JsonObject object) {
		this.deserializedFactory = DeserializedFactoryJson.decompileJsonObject(object, data);
		return this;
	}
}
