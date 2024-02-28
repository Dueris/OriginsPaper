package me.dueris.genesismc.registry.registries;

import java.nio.file.Path;

import org.bukkit.NamespacedKey;

import me.dueris.genesismc.registry.Registerable;

public class DatapackRepository implements Registerable{
    private NamespacedKey key;
    private Path path;

    public DatapackRepository(NamespacedKey key, Path path){
        this.key = key;
        this.path = path;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    public Path getPath(){
        return this.path;
    }
    
}
