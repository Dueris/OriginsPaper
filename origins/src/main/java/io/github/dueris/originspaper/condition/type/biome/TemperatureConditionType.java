package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biome;

public class TemperatureConditionType {

	public static boolean condition(Holder<Biome> biomeEntry, Comparison comparison, float compareTo) {
		return comparison.compare(biomeEntry.value().getBaseTemperature(), compareTo);
	}

	public static ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("temperature"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, posAndBiome) -> condition(posAndBiome.getB(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
