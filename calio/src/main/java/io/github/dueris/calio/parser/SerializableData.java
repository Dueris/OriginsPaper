package io.github.dueris.calio.parser;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.util.holder.ObjectTiedEnumState;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SerializableData {
	protected final Map<String, Object> defaultMap = new HashMap<>();
	private final HashMap<String, ObjectTiedEnumState<SerializableDataBuilder<?>>> dataMap = new HashMap<>();
	private final Object2IntOpenHashMap<String> keyPriorities = new Object2IntOpenHashMap<>();
	public Consumer<Instance> postProcessor;
	@Nullable
	protected ResourceLocation typedInstance;
	private int priorityCounter = 0;

	public static @NotNull SerializableData serializableData() {
		return new SerializableData();
	}

	public synchronized <T> SerializableData add(String key, @NotNull Tuple<Codec<T>, Class<T>> data) {
		return add(key, SerializableDataBuilder.of(data.getA(), data.getB()));
	}

	public synchronized SerializableData add(String key, SerializableDataBuilder<?> data) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.REQUIRED));
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized <T> SerializableData add(String key, @NotNull Tuple<Codec<T>, Class<T>> data, T defaultValue) {
		return add(key, SerializableDataBuilder.of(data.getA(), data.getB()), defaultValue);
	}

	public synchronized <T> SerializableData add(String key, SerializableDataBuilder<T> data, T defaultValue) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.DEFAULT));
		defaultMap.put(key, defaultValue);
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized <T> SerializableData add(String key, @NotNull Tuple<Codec<T>, Class<T>> data, Supplier<T> supplier) {
		return addSupplied(key, SerializableDataBuilder.of(data.getA(), data.getB()), supplier);
	}

	public synchronized <T> SerializableData addSupplied(String key, SerializableDataBuilder<T> data, @NotNull Supplier<T> supplier) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.DEFAULT));
		defaultMap.put(key, supplier.get());
		keyPriorities.put(key, priorityCounter++);
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

	public HashMap<String, ObjectTiedEnumState<SerializableDataBuilder<?>>> dataMap() {
		return dataMap;
	}

	public List<?> sortByPriorities(@NotNull List<Tuple<String, ?>> Tuples) {
		return Tuples.stream()
			.sorted(Comparator.comparingInt(Tuple -> keyPriorities.getInt(Tuple.getA())))
			.map(Tuple::getB)
			.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public record Instance(HashMap<String, Object> data) {

		public static <T> @NotNull Instance decompileJsonObject(JsonObject jsonObject, SerializableData definer, String instanceType, String key, Optional<Class<T>> classType) {
			Optional<Tuple<List<Tuple<String, ?>>, List<Tuple<String, ?>>>> compiledInstance = CalioParser.compileFromInstanceDefinition(
				definer, jsonObject, Optional.of(key + "=|=" + instanceType), classType
			);
			if (compiledInstance.isEmpty()) return new Instance(new HashMap<>());
			List<Tuple<String, ?>> compiledArguments = compiledInstance.get().getB();
			HashMap<String, Object> deserialized = new HashMap<>();
			for (Tuple<String, ?> compiledArgument : compiledArguments) {
				deserialized.put(compiledArgument.getA(), compiledArgument.getB());
			}

			return new Instance(deserialized);
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
}
