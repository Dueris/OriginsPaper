package me.dueris.calio.builder.inst;

import java.util.List;

import me.dueris.calio.registry.Registerable;

public interface FactoryBuilder extends Registerable{
    /**
     * Returns the current allowed instances for the Registerable
     */
    public List<FactoryObjectInstance> getValidObjectFactory();
}
