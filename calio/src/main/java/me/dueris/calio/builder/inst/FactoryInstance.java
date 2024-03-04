package me.dueris.calio.builder.inst;

import me.dueris.calio.registry.Registerable;
import me.dueris.calio.util.FactoryObjectInstance;
import org.json.simple.JSONObject;

import java.util.List;

public interface FactoryInstance extends Registerable {

    /**
     * Returns the current allowed instances for the Registerable
     */
    public List<FactoryObjectInstance> getValidInstances();
}
