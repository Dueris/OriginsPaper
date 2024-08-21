package io.github.dueris.calio.data;

import io.github.dueris.calio.parser.ParsingStrategy;
import io.github.dueris.calio.registry.RegistryKey;

import java.util.List;

public record AccessorKey<T>(List<String> modid, String folder, Class<T> toBuild, int priority,
							 ParsingStrategy strategy,
							 RegistryKey<T> registryKey) {
}
