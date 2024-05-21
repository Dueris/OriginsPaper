package me.dueris.calio.registry;

public interface IRegistry {

	/**
	 * Retrieves a Registrar using the provided NamespacedKey.
	 *
	 * @param key the key used to retrieve the Registrar
	 * @return the retrieved Registrar
	 */
	<T extends Registrable> Registrar<T> retrieve(RegistryKey<T> key);

	/**
	 * Creates a new instance of the specified class using the provided constructor and arguments.
	 *
	 * @param key       the namespaced key to create
	 * @param registrar the registrar to use for creating the instance
	 */
	<T extends Registrable> void create(RegistryKey<T> key, Registrar<T> registrar);

	/**
	 * Clears all the registries.
	 */
	void clearRegistries();
}