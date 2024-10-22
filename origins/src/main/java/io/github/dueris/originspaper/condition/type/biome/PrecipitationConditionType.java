package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biome;

public class PrecipitationConditionType {

	public static boolean condition(BlockPos pos, Holder<Biome> biomeEntry, Biome.Precipitation precipitation) {
		return biomeEntry.value().getPrecipitationAt(pos) == precipitation;
	}

	public static ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("precipitation"),
			new SerializableData()
				.add("precipitation", SerializableDataType.enumValue(Biome.Precipitation.class)),
			(data, posAndBiome) -> condition(posAndBiome.getA(), posAndBiome.getB(),
				data.get("precipitation")
			)
		);
	}

}
