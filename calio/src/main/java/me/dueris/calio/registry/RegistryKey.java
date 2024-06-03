package me.dueris.calio.registry;

import org.bukkit.NamespacedKey;

public record RegistryKey<T extends Registrable>(Class<T> type, NamespacedKey namespacedKey) {
}
