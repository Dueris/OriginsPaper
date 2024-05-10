package me.dueris.calio.builder.inst;

import org.bukkit.NamespacedKey;

public interface FactoryHolder {
	static FactoryData registerComponents(FactoryData data) {
		return new FactoryData();
	}

	public NamespacedKey ofResourceLocation(NamespacedKey key);
}
