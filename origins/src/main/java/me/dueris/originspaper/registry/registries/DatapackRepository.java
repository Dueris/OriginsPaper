package me.dueris.originspaper.registry.registries;

import me.dueris.calio.registry.Registrable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public record DatapackRepository(ResourceLocation key, Path path) implements Registrable {
}
