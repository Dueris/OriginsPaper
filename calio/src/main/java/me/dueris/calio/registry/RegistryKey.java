package me.dueris.calio.registry;


import net.minecraft.resources.ResourceLocation;

public record RegistryKey<T extends Registrable>(Class<T> type, ResourceLocation namespacedKey) {
}
