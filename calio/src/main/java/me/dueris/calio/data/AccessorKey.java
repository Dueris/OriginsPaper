package me.dueris.calio.data;

import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.RegistryKey;

public class AccessorKey<T extends Registrable> {
	private final String directory;
	private final boolean usesTypeDefiner;
	private final int priority;
	private final RegistryKey<T> registryKey;
	private Class<? extends FactoryHolder> ofType;

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, RegistryKey<T> registryKey) {
		this.directory = directory;
		this.usesTypeDefiner = usesTypeDefiner;
		this.ofType = null;
		this.priority = priority;
		this.registryKey = registryKey;
	}

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, RegistryKey<T> registryKey, Class<? extends FactoryHolder> ofType) {
		this(directory, priority, usesTypeDefiner, registryKey);
		this.ofType = ofType;
	}

	public String getDirectory() {
		return directory;
	}

	public boolean usesTypeDefiner() {
		return usesTypeDefiner;
	}

	public Class<? extends FactoryHolder> getOfType() {
		return ofType;
	}

	public int getPriority() {
		return priority;
	}

	public RegistryKey<T> getRegistryKey() {
		return this.registryKey;
	}
}
