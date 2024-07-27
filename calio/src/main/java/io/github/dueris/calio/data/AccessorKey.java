package io.github.dueris.calio.data;

import io.github.dueris.calio.parser.ParsingStrategy;
import io.github.dueris.calio.registry.RegistryKey;

public record AccessorKey<T>(String folder, Class<T> toBuild, int priority, ParsingStrategy strategy,
							 RegistryKey<T> registryKey) {
}
