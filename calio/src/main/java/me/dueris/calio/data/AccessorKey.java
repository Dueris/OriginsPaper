package me.dueris.calio.data;

import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.RegistryKey;

import javax.annotation.Nullable;

public class AccessorKey<T extends Registrable> {
	private final String directory;
	private final boolean usesTypeDefiner;
	private final int priority;
	private final RegistryKey<T> registryKey;
	private Class<? extends FactoryHolder> ofType;
	private String defaultType;

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, RegistryKey<T> registryKey) {
		this.directory = directory;
		this.usesTypeDefiner = usesTypeDefiner;
		this.ofType = null;
		this.defaultType = null;
		this.priority = priority;
		this.registryKey = registryKey;
	}

	public AccessorKey(String directory, int priority, boolean usesTypeDefiner, RegistryKey<T> registryKey, Class<? extends FactoryHolder> ofType) {
		this(directory, priority, usesTypeDefiner, registryKey);
		this.ofType = ofType;
	}

	public AccessorKey(
		String directory, int priority, boolean usesTypeDefiner, RegistryKey<T> registryKey, Class<? extends FactoryHolder> ofType, @Nullable String defaultType
	) {
		this(directory, priority, usesTypeDefiner, registryKey, ofType);
		this.defaultType = defaultType;
	}

	public String getDirectory() {
		return this.directory;
	}

	public boolean usesTypeDefiner() {
		return this.usesTypeDefiner;
	}

	public Class<? extends FactoryHolder> getOfType() {
		return this.ofType;
	}

	public int getPriority() {
		return this.priority;
	}

	public RegistryKey<T> getRegistryKey() {
		return this.registryKey;
	}

	@Nullable
	public String getDefaultType() {
		return this.defaultType;
	}
}
