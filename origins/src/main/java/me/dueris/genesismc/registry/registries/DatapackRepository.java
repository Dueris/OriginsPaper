package me.dueris.genesismc.registry.registries;

import me.dueris.calio.registry.Registrable;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

public class DatapackRepository implements Registrable {
    private final NamespacedKey key;
    private final Path path;

    @ApiStatus.Internal
    public DatapackRepository(NamespacedKey key, Path path) {
        this.key = key;
        this.path = path;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    public Path getPath() {
        return this.path;
    }

}
