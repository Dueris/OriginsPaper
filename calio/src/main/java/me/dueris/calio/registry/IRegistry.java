package me.dueris.calio.registry;

public interface IRegistry {
	<T extends Registrable> Registrar<T> retrieve(RegistryKey<T> var1);

	<T extends Registrable> void create(RegistryKey<T> var1, Registrar<T> var2);

	void clearRegistries();
}
