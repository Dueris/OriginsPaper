package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public record BiomeConditionContext(BlockPos pos, Holder<Biome> biomeEntry) implements TypeConditionContext {

}
