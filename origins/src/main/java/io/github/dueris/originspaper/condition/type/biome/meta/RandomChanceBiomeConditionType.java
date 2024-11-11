package io.github.dueris.originspaper.condition.type.biome.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class RandomChanceBiomeConditionType extends BiomeConditionType implements RandomChanceMetaConditionType {

	private final float chance;

	public RandomChanceBiomeConditionType(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
		return testCondition();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiomeConditionTypes.RANDOM_CHANCE;
	}

	@Override
	public float chance() {
		return chance;
	}

}
