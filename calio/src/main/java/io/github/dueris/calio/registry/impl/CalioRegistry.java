package io.github.dueris.calio.registry.impl;

import com.google.common.base.Preconditions;
import io.github.dueris.calio.registry.IRegistry;
import io.github.dueris.calio.registry.Registrar;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.exceptions.AlreadyRegisteredException;

import java.util.HashMap;
import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
public class CalioRegistry implements IRegistry {
	public static final CalioRegistry INSTANCE = new CalioRegistry();
	private final HashMap<RegistryKey<?>, Registrar<?>> registry = new LinkedHashMap<>();

	public void freezeAll() {
		this.registry.values().forEach(Registrar::freeze);
	}

	@Override
	public <T> Registrar<T> retrieve(RegistryKey<T> key) {
		Preconditions.checkArgument(key != null, "RegistryKey must not be null");
		if (!this.registry.containsKey(key)) {
			create(key, new Registrar<>(key.type()));
		}
		return (Registrar<T>) this.registry.get(key);
	}

	@Override
	public <T> Registrar<T> create(RegistryKey<T> key, Registrar<T> registrar) {
		Preconditions.checkArgument(!this.registry.containsKey(key), new AlreadyRegisteredException("Cannot register a key that's already registered"));
		Preconditions.checkArgument(
			!this.registry.containsValue(registrar), new AlreadyRegisteredException("Cannot register a registrar that's already registered")
		);
		return (Registrar<T>) this.registry.put(key, registrar);
	}

	@Override
	public void clearRegistries() {
		this.registry.values().forEach(Registrar::clearEntries);
	}
}
