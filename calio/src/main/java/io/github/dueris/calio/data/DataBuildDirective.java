package io.github.dueris.calio.data;

import io.github.dueris.calio.parser.RootResult;
import io.github.dueris.calio.registry.RegistryKey;

import java.util.List;

public record DataBuildDirective<T>(List<String> modids, String folder, SerializableDataBuilder<RootResult<T>> builder,
									int priority, RegistryKey<T> registryKey) {
}
