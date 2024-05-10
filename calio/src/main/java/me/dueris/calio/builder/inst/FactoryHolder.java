package me.dueris.calio.builder.inst;

import org.bukkit.NamespacedKey;

import me.dueris.calio.registry.Registrable;

public interface FactoryHolder extends Registrable {
	static FactoryData registerComponents(FactoryData data) {
		return new FactoryData();
	}

	public NamespacedKey ofResourceLocation(NamespacedKey key);

	public default void bootstrap() { }
}
