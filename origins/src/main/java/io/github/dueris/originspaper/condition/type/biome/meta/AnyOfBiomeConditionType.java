package io.github.dueris.originspaper.condition.type.biome.meta;

import io.github.dueris.originspaper.condition.BiomeCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.context.BiomeConditionContext;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnyOfBiomeConditionType extends BiomeConditionType implements AnyOfMetaConditionType<BiomeConditionContext, BiomeCondition> {

	private final List<BiomeCondition> conditions;

	public AnyOfBiomeConditionType(List<BiomeCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
		return testConditions(new BiomeConditionContext(pos, biomeEntry));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiomeConditionTypes.ANY_OF;
	}

	@Override
	public List<BiomeCondition> conditions() {
		return conditions;
	}

}
