package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biome;

public class InTagConditionType {

	public static boolean condition(Holder<Biome> biomeEntry, TagKey<Biome> biomeTag) {
		return biomeEntry.is(biomeTag);
	}

	public static ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			new SerializableData()
				.add("tag", SerializableDataTypes.BIOME_TAG),
			(data, posAndBiome) -> condition(posAndBiome.getB(),
				data.get("tag")
			)
		);
	}

}
