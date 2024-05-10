package me.dueris.calio.builder.inst;

import org.bukkit.NamespacedKey;

public class AccessorKey {
	private final String directory;
	private final boolean usesTypeDefiner;
	private final int priority;
	private Class<? extends FactoryHolder> ofType;
	private final NamespacedKey registryKey;

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, NamespacedKey registryKey) {
		this.directory = directory;
		this.usesTypeDefiner = usesTypeDefiner;
		this.ofType = null;
		this.priority = priority;
		this.registryKey = registryKey;
	}

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, NamespacedKey registryKey, Class<? extends FactoryHolder> ofType) {
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

	public NamespacedKey getRegistryKey() {
		return this.registryKey;
	}
}
