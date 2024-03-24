package me.dueris.calio.builder;

import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.FactoryInstance;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class CalioBuilder {
    public static CalioBuilder INSTANCE = new CalioBuilder();
    public List<AccessorRoot> accessorRoots = new ArrayList<>();

    /**
     * Adds a new AccessorRoot object to the accessorRoots list.
     *
     * @param dirPath     the directory path for the AccessorRoot
     * @param putRegistry the namespaced key for the AccessorRoot
     * @param inst        the factory instance for the AccessorRoot
     * @param priority    the priority of the AccessorRoot
     */
    public void addAccessorRoot(String dirPath, NamespacedKey putRegistry, FactoryInstance inst, int priority) {
        accessorRoots.add(new AccessorRoot(dirPath, putRegistry, inst, priority));
    }
}
