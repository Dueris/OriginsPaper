package me.dueris.calio.registry;

import org.bukkit.NamespacedKey;

public interface IRegistry {

    /**
     * Retrieves a Registrar using the provided NamespacedKey.
     *
     * @param key the key used to retrieve the Registrar
     * @return the retrieved Registrar
     */
    Registrar retrieve(NamespacedKey key);

    /**
     * Creates a new instance of the specified class using the provided constructor and arguments.
     *
     * @param key       the namespaced key to create
     * @param registrar the registrar to use for creating the instance
     */
    void create(NamespacedKey key, Registrar registrar);

    /**
     * Clears all the registries.
     */
    void clearRegistries();
}