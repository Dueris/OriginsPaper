package me.dueris.calio.data;

import me.dueris.calio.registry.Registrable;
import org.bukkit.NamespacedKey;

public interface FactoryHolder extends Registrable {
	static FactoryData registerComponents(FactoryData data) {
		return new FactoryData();
	}

	public FactoryHolder ofResourceLocation(NamespacedKey key);

	public default void bootstrap() {
	}

	public default boolean canRegister() {
		return true;
	}
}
