package io.github.dueris.calio.registry;

import net.minecraft.resources.ResourceLocation;

public record RegistryKey<T>(Class<T> type, ResourceLocation namespacedKey) {
}
