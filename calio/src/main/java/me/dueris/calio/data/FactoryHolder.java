package me.dueris.calio.data;

import me.dueris.calio.registry.Registrable;
import org.bukkit.NamespacedKey;

public interface FactoryHolder extends Registrable {
	static FactoryData registerComponents(FactoryData data) {
		return data;
	}

	FactoryHolder ofResourceLocation(NamespacedKey key);

	default void bootstrap() {
	}

	default boolean canRegister() {
		return true;
	}
}
