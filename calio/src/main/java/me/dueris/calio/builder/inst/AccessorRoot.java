package me.dueris.calio.builder.inst;

import org.bukkit.NamespacedKey;

public class AccessorRoot {
    private String dirPath;
    private NamespacedKey putRegistry;
    private FactoryInstance inst;
    private int priority;

    public AccessorRoot(String dirPath, NamespacedKey putRegistry, FactoryInstance inst, int priority) {
        this.dirPath = dirPath;
        this.putRegistry = putRegistry;
        this.inst = inst;
        this.priority = priority;
    }

    /**
     * Retrieves the directory path.
     *
     * @return the directory path
     */
    public String getDirectoryPath() {
        return this.dirPath;
    }

    /**
     * Retrieves the FactoryInstance.
     *
     * @return the FactoryInstance
     */
    public FactoryInstance getFactoryInst() {
        return this.inst;
    }

    /**
     * Returns the value of the putRegistry field.
     *
     * @return the value of the putRegistry field
     */
    public NamespacedKey getPutRegistry() {
        return this.putRegistry;
    }

    /**
     * Returns the priority of the object for registry processing.
     *
     * @return the priority of the object
     */
    public int getPriority() {
        return this.priority;
    }
}
