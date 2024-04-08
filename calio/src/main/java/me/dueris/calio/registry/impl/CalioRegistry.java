package me.dueris.calio.registry.impl;

import com.google.common.base.Preconditions;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.exceptions.AlreadyRegisteredException;
import org.bukkit.NamespacedKey;

import java.util.HashMap;

public class CalioRegistry implements IRegistry {
    public static CalioRegistry INSTANCE = new CalioRegistry();
    private final HashMap<NamespacedKey, Registrar> registry = new HashMap<>();

    @Override
    public Registrar retrieve(NamespacedKey key) {
        Preconditions.checkArgument(key != null, "NamespacedKey must not be null");
        return registry.get(key);
    }

    @Override
    public void create(NamespacedKey key, Registrar registrar) {
        Preconditions.checkArgument(!registry.containsKey(key), new AlreadyRegisteredException("Cannot register a key thats already registered"));
        Preconditions.checkArgument(!registry.containsValue(registrar), new AlreadyRegisteredException("Cannot register an registrar thats already registered"));
        registry.put(key, registrar);
    }

    public void freezeAll() {
        registry.values().forEach(registrar -> {
            registrar.freeze();
        });
    }

    @Override
    public void clearRegistries() {
        registry.clear();
    }

}
