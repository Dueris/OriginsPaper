package io.github.dueris.originspaper.condition.type.biome.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class ConstantBiomeConditionType extends BiomeConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantBiomeConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
		return value();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiomeConditionTypes.CONSTANT;
	}

	@Override
	public boolean value() {
		return value;
	}

}
