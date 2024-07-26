package me.dueris.calio.registry;

import com.google.common.base.Preconditions;
import me.dueris.calio.registry.exceptions.UnmodifiableRegistryException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Registrar<T extends Registrable> {
	private final Class<T> ofType;
	public ConcurrentHashMap<ResourceLocation, T> rawRegistry = new ConcurrentHashMap<>();
	private boolean frozen = false;

	public Registrar(Class<T> ofType) {
		this.ofType = ofType;
	}

	public T register(T item) {
		this.checkFrozen();

		try {
			this.registerOrThrow(item);
		} catch (Exception ignored) {
		}

		return item;
	}

	public void registerOrThrow(@NotNull T item) {
		this.checkFrozen();
		Preconditions.checkArgument(item.key() != null, "Registrable key cannot be null");
		this.rawRegistry.put(item.key(), item);
	}

	public void replaceEntry(ResourceLocation currentKey, T newValue) {
		if (!this.containsRegistrable(newValue)) {
			if (this.rawRegistry.containsKey(currentKey)) {
				this.rawRegistry.remove(currentKey);
				this.rawRegistry.put(currentKey, newValue);
			}
		}
	}

	public void checkFrozen() {
		if (this.frozen) {
			throw new UnmodifiableRegistryException("Registry already frozen!");
		}
	}

	public T get(ResourceLocation key) {
		return this.rawRegistry.get(key);
	}

	public boolean containsRegistrable(T item) {
		return this.rawRegistry.containsValue(item);
	}

	public void removeFromRegistry(ResourceLocation key) {
		this.rawRegistry.remove(key);
	}

	public Optional<T> getOptional(ResourceLocation key) {
		return this.rawRegistry.containsKey(key) ? Optional.of(this.get(key)) : Optional.empty();
	}

	public Collection<T> values() {
		return this.rawRegistry.values();
	}

	public T[] getFromPredicate(Predicate<T> predicate) {
		ArrayList<T> tL = new ArrayList<>();
		this.rawRegistry.values().forEach(regI -> {
			if (predicate.test(regI)) {
				tL.add(regI);
			}
		});
		T[] array = (T[]) (new Object[tL.size()]);
		IntStream.range(0, tL.size()).forEach(i -> array[i] = tL.get(i));
		return array;
	}

	public Stream<T> stream() {
		return this.rawRegistry.values().stream();
	}

	public void forEach(BiConsumer<ResourceLocation, T> consumer) {
		this.rawRegistry.forEach(consumer);
	}

	public void freeze() {
		this.frozen = true;
	}

	public void clearEntries() {
		this.rawRegistry.clear();
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
}
