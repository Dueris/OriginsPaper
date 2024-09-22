package io.github.dueris.calio.data;

import io.github.dueris.calio.parser.RootResult;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DataBuildDirective<T>(List<String> modids, String folder, SerializableDataType<RootResult<T>> builder,
									int priority, @Nullable Registry<T> registryKey) {
}
