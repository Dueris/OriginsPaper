package me.dueris.calio.builder.inst;

import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;

import org.bukkit.NamespacedKey;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;

public interface FactoryInstance extends Registerable {

    /**
     * Returns the current allowed instances for the Registerable
     */
    public List<FactoryObjectInstance> getValidObjectFactory();

    public void createInstance(FactoryProvider obj, File rawFile, Registrar<? extends Registerable> registry, NamespacedKey namespacedTag);
}
