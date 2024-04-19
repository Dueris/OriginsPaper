package me.dueris.calio.registry;

import org.bukkit.NamespacedKey;

public interface Registrable {

    /**
     * Retrieves the namespaced key(or identifier) associated with this object.
     *
     * @return the namespaced key
     */
    NamespacedKey getKey();
}
