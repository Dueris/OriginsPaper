package me.dueris.calio.registry;

import org.bukkit.NamespacedKey;

public final record RegistryKey<T extends Registrable>(Class<T> type, NamespacedKey namespacedKey) {}
