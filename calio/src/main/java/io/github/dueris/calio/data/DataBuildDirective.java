package io.github.dueris.calio.data;

import io.github.dueris.calio.parser.RootResult;
import net.minecraft.core.Registry;

import java.util.List;

public record DataBuildDirective<T>(List<String> modids, String folder, SerializableDataType<RootResult<T>> builder,
									int priority, Registry<T> registryKey) {
}
