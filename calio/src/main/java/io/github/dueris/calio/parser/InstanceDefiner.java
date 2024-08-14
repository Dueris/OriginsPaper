package io.github.dueris.calio.parser;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.ObjectTiedEnumState;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InstanceDefiner {
	protected final Map<String, Object> defaultMap = new HashMap<>();
	private final HashMap<String, ObjectTiedEnumState<SerializableDataBuilder<?>>> dataMap = new HashMap<>();
	private final Object2IntOpenHashMap<String> keyPriorities = new Object2IntOpenHashMap<>();
	@Nullable
	protected ResourceLocation typedInstance;
	private int priorityCounter = 0;

	@Contract(value = " -> new", pure = true)
	public static @NotNull InstanceDefiner instanceDefiner() {
		return new InstanceDefiner();
	}

	public synchronized InstanceDefiner add(String key, SerializableDataBuilder<?> data) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.REQUIRED));
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized <T> InstanceDefiner add(String key, SerializableDataBuilder<T> data, T defaultValue) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.DEFAULT));
		defaultMap.put(key, defaultValue);
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized <T> InstanceDefiner addSupplied(String key, SerializableDataBuilder<T> data, @NotNull Supplier<T> supplier) {
		dataMap.put(key, new ObjectTiedEnumState<>(data, SerializableType.DEFAULT));
		defaultMap.put(key, supplier.get());
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized InstanceDefiner typedRegistry(ResourceLocation type) {
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
}
