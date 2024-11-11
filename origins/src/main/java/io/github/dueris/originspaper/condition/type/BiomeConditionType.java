package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.BiomeCondition;
import io.github.dueris.originspaper.condition.context.BiomeConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public abstract class BiomeConditionType extends AbstractConditionType<BiomeConditionContext, BiomeCondition> {

	@Override
	public boolean test(BiomeConditionContext context) {
		return test(context.pos(), context.biomeEntry());
	}

	@Override
	public BiomeCondition createCondition(boolean inverted) {
		return new BiomeCondition(this, inverted);
	}

	public abstract boolean test(BlockPos pos, Holder<Biome> biomeEntry);

}
