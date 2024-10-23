package io.github.dueris.calio.data;

import net.minecraft.core.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DataBuildDirective<T>(List<String> modids, String folder, SerializableDataType<T> builder,
									int priority, @Nullable Registry<T> registryKey) {
}
