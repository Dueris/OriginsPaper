package me.dueris.calio.builder.inst;

import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.List;

public interface FactoryInstance extends Registerable {

    /**
     * Returns the current allowed instances for the Registerable
     */
    List<FactoryObjectInstance> getValidObjectFactory();

    /**
     * Creates an instance of the FactoryProvider class using the provided raw file, registry, and namespaced tag associated with the instance being created.
     *
     * @param obj           the FactoryProvider object to create an instance of
     * @param rawFile       the raw file to use for creating the instance
     * @param registry      the registry to use for creating the instance
     * @param namespacedTag the namespaced tag to use for creating the instance
     */
    void createInstance(FactoryProvider obj, File rawFile, Registrar<? extends Registerable> registry, NamespacedKey namespacedTag);
}
