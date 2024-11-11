package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class HighHumidityBiomeConditionType extends BiomeConditionType {

	@Override
	public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
		return biomeEntry.value().climateSettings.downfall() > 0.85F;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiomeConditionTypes.HIGH_HUMIDITY;
	}

}
