package me.dueris.originspaper.registry.registries;

import me.dueris.calio.registry.Registrable;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public record DatapackRepository(NamespacedKey key, Path path) implements Registrable {
}
