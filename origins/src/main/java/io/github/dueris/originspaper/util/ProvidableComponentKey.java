package io.github.dueris.originspaper.util;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProvidableComponentKey<T> extends ConcurrentHashMap<Entity, T> {

	public synchronized boolean isProvidedBy(Entity provider) {
		return this.containsKey(provider);
	}

	public synchronized @Nullable T getNullable(Entity provider) {
		if (provider == null) return null;
		return this.getOrDefault(provider, null);
	}

	public synchronized @NotNull Optional<T> maybeGet(Entity provider) {
		return Optional.ofNullable(this.getNullable(provider));
	}
}
