package io.github.dueris.calio.parser.reader;

import com.google.gson.JsonObject;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DeserializedFactoryJson {
	private final Map<String, Object> keyValueMap;

	public DeserializedFactoryJson(Map<String, Object> keyValueMap) {
		this.keyValueMap = keyValueMap;
	}

	public static @Nullable DeserializedFactoryJson decompileJsonObject(JsonObject jsonObject, InstanceDefiner definer) {
		Optional<Pair<List<Pair<String, ?>>, List<Pair<String, ?>>>> compiledInstance = CalioParser.compileFromInstanceDefinition(
			definer, jsonObject, Optional.empty(), Optional.empty()
		);
		if (compiledInstance.isEmpty()) return null;
		List<Pair<String, ?>> compiledArguments = compiledInstance.get().second();
		Map<String, Object>

		return new DeserializedFactoryJson();
	}

	private final HashMap<String, Object> data = new HashMap<>();

	public boolean isPresent(String name) {
		return data.containsKey(name);

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

		if(!data.containsKey(name)) {
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
