package me.dueris.calio.registry.impl;

import com.google.common.base.Preconditions;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.RegistryKey;
import me.dueris.calio.registry.exceptions.AlreadyRegisteredException;

import java.util.HashMap;

public class CalioRegistry implements IRegistry {
	public static final CalioRegistry INSTANCE = new CalioRegistry();
	private final HashMap<RegistryKey<?>, Registrar<?>> registry = new HashMap<>();

	public void freezeAll() {
		registry.values().forEach(Registrar::freeze);
	}

	@Override
	public <T extends Registrable> Registrar<T> retrieve(RegistryKey<T> key) {
		Preconditions.checkArgument(key != null, "NamespacedKey must not be null");
		return (Registrar<T>) registry.get(key);
	}

	@Override
	public <T extends Registrable> void create(RegistryKey<T> key, Registrar<T> registrar) {
		Preconditions.checkArgument(!registry.containsKey(key), new AlreadyRegisteredException("Cannot register a key thats already registered"));
		Preconditions.checkArgument(!registry.containsValue(registrar), new AlreadyRegisteredException("Cannot register an registrar thats already registered"));
		registry.put(key, registrar);
	}

	@Override
	public void clearRegistries() {
		registry.values().forEach(Registrar::clearEntries);
	}

}
