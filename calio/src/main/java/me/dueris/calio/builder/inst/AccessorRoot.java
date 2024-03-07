package me.dueris.calio.builder.inst;

import org.bukkit.NamespacedKey;

public class AccessorRoot {
	private String dirPath;
	private NamespacedKey putRegistry;
	private FactoryInstance inst;
	private int priority;

	public AccessorRoot(String dirPath, NamespacedKey putRegistry, FactoryInstance inst, int priority) {
		this.dirPath = dirPath;
		this.putRegistry = putRegistry;
		this.inst = inst;
		this.priority = priority;
	}

	public String getDirectoryPath() {
		return this.dirPath;
	}

	public FactoryInstance getFactoryInst() {
		return this.inst;
	}

	public NamespacedKey getPutRegistry() {
		return this.putRegistry;
	}

	public int getPriority() {
		return this.priority;
	}
}
