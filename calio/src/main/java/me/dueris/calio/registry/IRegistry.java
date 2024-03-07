package me.dueris.calio.registry;

import org.bukkit.NamespacedKey;

public interface IRegistry {
	Registrar retrieve(NamespacedKey key);

	void create(NamespacedKey key, Registrar registrar);

	void clearRegistries();
}