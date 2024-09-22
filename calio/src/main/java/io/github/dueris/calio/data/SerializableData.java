package io.github.dueris.calio.data;

import com.mojang.serialization.Codec;
import io.github.dueris.calio.parser.SerializableType;
import io.github.dueris.calio.util.holder.ObjectTiedEnumState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SerializableData {
	public Consumer<Instance> postProcessor;
	public ResourceLocation typedInstance;
	Map<String, Object> defaultMap = new LinkedHashMap<>();
	HashMap<String, ObjectTiedEnumState<SerializableDataType<?>>> dataMap = new LinkedHashMap<>();

	public SerializableData(@NotNull SerializableData serializableData) {
		this.defaultMap = serializableData.defaultMap;
		this.dataMap = serializableData.dataMap;
		this.postProcessor = serializableData.postProcessor;
		this.typedInstance = serializableData.typedInstance;
	}

	public SerializableData() {
	}

	public static @NotNull SerializableData serializableData() {
		return new SerializableData();
	}

	public synchronized <T> SerializableData add(String key, @NotNull Tuple<Codec<T>, Class<T>> data) {
		return add(key, SerializableDataType.of(data.getA(), data.getB()));
	}

	public synchronized SerializableData add(String key, SerializableDataType<?> data) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.REQUIRED));
		return this;
	}

	public synchronized <T> SerializableData add(String key, @NotNull Tuple<Codec<T>, Class<T>> data, T defaultValue) {
		return add(key, SerializableDataType.of(data.getA(), data.getB()), defaultValue);
	}

	public synchronized <T> SerializableData add(String key, SerializableDataType<T> data, T defaultValue) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.DEFAULT));
		defaultMap.put(key, defaultValue);
		return this;
	}

	public synchronized <T> SerializableData add(String key, @NotNull Tuple<Codec<T>, Class<T>> data, Supplier<T> supplier) {
		return addSupplied(key, SerializableDataType.of(data.getA(), data.getB()), supplier);
	}

	public synchronized <T> SerializableData addSupplied(String key, SerializableDataType<T> data, @NotNull Supplier<T> supplier) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.DEFAULT));
		defaultMap.put(key, supplier.get());
		return this;
	}

	public synchronized SerializableData postProcessor(Consumer<Instance> processor) {
		this.postProcessor = processor;
		return this;
	}

	public synchronized SerializableData typedRegistry(ResourceLocation type) {
		this.typedInstance = type;
		return this;
	}

	public HashMap<String, ObjectTiedEnumState<SerializableDataType<?>>> dataMap() {
		return dataMap;
	}

	@SuppressWarnings("unchecked")
	public record Instance(Map<String, Object> data) {

		public boolean isPresent(String name) {
			return data.containsKey(name) && data.get(name) != null;
		}

		public <T> void ifPresent(String name, Consumer<T> consumer) {
			if (isPresent(name)) {
				consumer.accept(get(name));
			}
		}

		public Instance set(String name, Object value) {
			this.data.put(name, value);
			return this;
		}

		public <T> T get(String name) {

			if (!data.containsKey(name)) {
				throw new RuntimeException("Tried to get field \"" + name + "\" from data, which did not exist.");
			}

			return (T) data.get(name);
		}

		public <T> T getOrDefault(String name, T def) {
			if (isPresent(name)) {
				return get(name);
			}

			return def;
		}

		public <T> T getOrElse(String name, T object) {
			return getOrDefault(name, object);
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
}
