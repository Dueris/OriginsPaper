package io.github.dueris.calio.registry;

public interface IRegistry {
	<T> Registrar<T> retrieve(RegistryKey<T> var1);

	<T> Registrar<T> create(RegistryKey<T> var1, Registrar<T> var2);

	void clearRegistries();
}
