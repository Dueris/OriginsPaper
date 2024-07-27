package me.dueris.originspaper.registry.registries;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.nio.file.Path;

@Internal
public record DatapackRepository(ResourceLocation key, Path path) {
}
