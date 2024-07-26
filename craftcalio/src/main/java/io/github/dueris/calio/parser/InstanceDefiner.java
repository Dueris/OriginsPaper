package io.github.dueris.calio.parser;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.util.holder.ObjectTiedBoolean;
import io.github.dueris.calio.util.holder.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceDefiner {
	protected final Map<String, Object> defaultMap = new HashMap<>();
	private final HashMap<String, ObjectTiedBoolean<SerializableDataBuilder<?>>> dataMap = new HashMap<>();
	private final Object2IntOpenHashMap<String> keyPriorities = new Object2IntOpenHashMap<>();
	@Nullable
	protected ResourceLocation typedInstance;
	private int priorityCounter = 0;

	@Contract(value = " -> new", pure = true)
	public static @NotNull InstanceDefiner instanceDefiner() {
		return new InstanceDefiner();
	}

	public synchronized InstanceDefiner add(String key, SerializableDataBuilder<?> data) {
		dataMap.put(key, new ObjectTiedBoolean<>(data, false, false));
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized <T> InstanceDefiner add(String key, SerializableDataBuilder<T> data, T defaultValue) {
		dataMap.put(key, new ObjectTiedBoolean<>(data, false, true));
		defaultMap.put(key, defaultValue);
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized InstanceDefiner required(String key, SerializableDataBuilder<?> data) {
		dataMap.put(key, new ObjectTiedBoolean<>(data, true, false));
		keyPriorities.put(key, priorityCounter++);
		return this;
	}

	public synchronized InstanceDefiner typedRegistry(ResourceLocation type) {
		this.typedInstance = type;
		return this;
	}

	public HashMap<String, ObjectTiedBoolean<SerializableDataBuilder<?>>> dataMap() {
		return dataMap;
	}

	protected List<?> sortByPriorities(@NotNull List<Pair<String, ?>> pairs) {
		return pairs.stream()
			.sorted(Comparator.comparingInt(pair -> keyPriorities.getInt(pair.first())))
			.map(Pair::second)
			.toList();
	}
}
