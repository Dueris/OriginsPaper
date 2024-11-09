package io.github.dueris.originspaper.util;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ProvidableComponentKey<T, E extends Entity> extends ConcurrentHashMap<E, T> {
	private final Function<E, T> defaultValue;

	public ProvidableComponentKey() {
		this(null);
	}

	public ProvidableComponentKey(Function<E, T> defaultValue) {
		this.defaultValue = defaultValue;
	}

	public synchronized boolean isProvidedBy(E provider) {
		return this.containsKey(provider);
	}

	public synchronized @Nullable T getNullable(E provider) {
		if (provider == null) return null;
		return this.getOrDefault(provider, null);
	}

	public synchronized @NotNull Optional<T> maybeGet(E provider) {
		return Optional.ofNullable(this.getNullable(provider));
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized T get(Object key) {
		if (!(key instanceof Entity)) {
			return super.get(key);
		}

		T value = super.get(key);
		if (value == null) {
			value = defaultValue.apply((E) key);
			putNullable((E) key, value);
		}
		return value;
	}

	public void putNullable(E key, T value) {
		if (key != null && value != null) {
			super.put(key, value);
		}
	}
}
