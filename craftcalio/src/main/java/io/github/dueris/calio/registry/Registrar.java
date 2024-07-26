package io.github.dueris.calio.registry;

import com.google.common.base.Preconditions;
import io.github.dueris.calio.registry.exceptions.UnmodifiableRegistryException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Registrar<T> {
	private final Class<T> ofType;
	private final ConcurrentHashMap<ResourceLocation, T> rawRegistry = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<T, ResourceLocation> reverseLookup = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, T> defaultValues = new ConcurrentHashMap<>();
	private final List<Consumer<RegistryEvent<T>>> eventListeners = Collections.synchronizedList(new ArrayList<>());
	private volatile boolean frozen = false;
	private BiFunction<ResourceLocation, T, String> serializer;
	private BiFunction<ResourceLocation, String, T> deserializer;

	public Registrar(Class<T> ofType) {
		this.ofType = ofType;
	}

	public synchronized T register(T item, ResourceLocation key) {
		this.checkFrozen();
		this.registerOrThrow(item, key);
		return item;
	}

	public synchronized void registerOrThrow(@NotNull T item, ResourceLocation key) {
		this.checkFrozen();
		Preconditions.checkArgument(key != null, "Registry key cannot be null");
		this.rawRegistry.put(key, item);
		this.reverseLookup.put(item, key);
		this.fireEvent(new RegistryEvent<>(RegistryEvent.Type.REGISTER, key, item));
	}

	public synchronized void replaceEntry(ResourceLocation currentKey, T newValue) {
		this.checkFrozen();
		if (!this.containsRegistrable(newValue)) {
			if (this.rawRegistry.containsKey(currentKey)) {
				T oldValue = this.rawRegistry.replace(currentKey, newValue);
				this.reverseLookup.remove(oldValue);
				this.reverseLookup.put(newValue, currentKey);
				this.fireEvent(new RegistryEvent<>(RegistryEvent.Type.REPLACE, currentKey, newValue));
			}
		}
	}

	private void checkFrozen() {
		if (this.frozen) {
			throw new UnmodifiableRegistryException("Registry already frozen!");
		}
	}

	public T get(ResourceLocation key) {
		return this.rawRegistry.getOrDefault(key, this.defaultValues.get(key));
	}

	public boolean containsRegistrable(T item) {
		return this.rawRegistry.containsValue(item);
	}

	public Optional<T> getOptional(ResourceLocation key) {
		return Optional.ofNullable(this.rawRegistry.getOrDefault(key, this.defaultValues.get(key)));
	}

	public Collection<T> values() {
		return this.rawRegistry.values();
	}

	@SuppressWarnings("unchecked")
	public T[] getFromPredicate(Predicate<T> predicate) {
		List<T> filtered = this.rawRegistry.values().stream()
			.filter(predicate)
			.toList();
		T[] array = (T[]) java.lang.reflect.Array.newInstance(ofType, filtered.size());
		return filtered.toArray(array);
	}

	public Stream<T> stream() {
		return this.rawRegistry.values().stream();
	}

	public void forEach(BiConsumer<ResourceLocation, T> consumer) {
		this.rawRegistry.forEach(consumer);
	}

	public synchronized void freeze() {
		this.frozen = true;
	}

	public synchronized void clearEntries() {
		this.rawRegistry.clear();
		this.reverseLookup.clear();
		this.fireEvent(new RegistryEvent<>(RegistryEvent.Type.CLEAR, null, null));
	}

	public boolean hasEntries() {
		return !this.rawRegistry.isEmpty();
	}

	public int registrySize() {
		return this.rawRegistry.size();
	}

	public Class<T> getRegisterableType() {
		return this.ofType;
	}

	public ResourceLocation getResourceLocation(T item) {
		return this.reverseLookup.get(item);
	}

	public void addEventListener(Consumer<RegistryEvent<T>> listener) {
		this.eventListeners.add(listener);
	}

	private void fireEvent(RegistryEvent<T> event) {
		for (Consumer<RegistryEvent<T>> listener : this.eventListeners) {
			listener.accept(event);
		}
	}

	public void setDefault(ResourceLocation key, T value) {
		this.defaultValues.put(key, value);
	}

	public void setSerializer(BiFunction<ResourceLocation, T, String> serializer) {
		this.serializer = serializer;
	}

	public void setDeserializer(BiFunction<ResourceLocation, String, T> deserializer) {
		this.deserializer = deserializer;
	}

	public Optional<String> serialize(ResourceLocation key) {
		if (serializer == null) {
			throw new UnsupportedOperationException("Serializer not set");
		}
		T value = this.rawRegistry.get(key);
		if (value == null) {
			return Optional.empty();
		}
		return Optional.of(serializer.apply(key, value));
	}

	public Optional<T> deserialize(ResourceLocation key, String data) {
		if (deserializer == null) {
			throw new UnsupportedOperationException("Deserializer not set");
		}
		T value = deserializer.apply(key, data);
		this.register(value, key);
		return Optional.of(value);
	}

	public record RegistryEvent<T>(Registrar.RegistryEvent.Type type, ResourceLocation key, T value) {
		public enum Type {
			REGISTER, REPLACE, CLEAR
		}

	}
}
