package io.github.dueris.calio.parser.reader;

import com.google.gson.JsonObject;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.InstanceDefiner;
import net.minecraft.util.Tuple;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record DeserializedFactoryJson(HashMap<String, Object> data) {

	public static @Nullable DeserializedFactoryJson decompileJsonObject(JsonObject jsonObject, InstanceDefiner definer, String instanceType, String key) {
		Optional<Tuple<List<Tuple<String, ?>>, List<Tuple<String, ?>>>> compiledInstance = CalioParser.compileFromInstanceDefinition(
			definer, jsonObject, Optional.of(key + "=|=" + instanceType), Optional.empty()
		);
		if (compiledInstance.isEmpty()) return null;
		List<Tuple<String, ?>> compiledArguments = compiledInstance.get().getB();
		HashMap<String, Object> deserialized = new HashMap<>();
		for (Tuple<String, ?> compiledArgument : compiledArguments) {
			deserialized.put(compiledArgument.getA(), compiledArgument.getB());
		}

		return new DeserializedFactoryJson(deserialized);
	}

	public boolean isPresent(String name) {
		return data.containsKey(name) && data.get(name) != null;
	}

	public <T> void ifPresent(String name, Consumer<T> consumer) {
		if (isPresent(name)) {
			consumer.accept(get(name));
		}
	}

	public void set(String name, Object value) {
		this.data.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {

		if (!data.containsKey(name)) {
			throw new RuntimeException("Tried to get field \"" + name + "\" from data, which did not exist.");
		}

		return (T) data.get(name);
	}

	public int getInt(String name) {
		return get(name);
	}

	public boolean getBoolean(String name) {
		return get(name);
	}

	public float getFloat(String name) {
		return get(name);
	}

	public double getDouble(String name) {
		return get(name);
	}

	public String getString(String name) {
		return get(name);
	}

	public ResourceLocation getId(String name) {
		return get(name);
	}

	public AttributeModifier getModifier(String name) {
		return get(name);
	}
}
