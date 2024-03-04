package me.dueris.calio.builder;

import me.dueris.calio.registry.Registerable;
import org.json.simple.JSONObject;

public interface FactoryInstance extends Registerable {
    /**
     * Returns if the values in the registry are currently valid
     * @param object
     * @return stuff
     */
    public boolean hasCorrectValues(JSONObject object);
}
