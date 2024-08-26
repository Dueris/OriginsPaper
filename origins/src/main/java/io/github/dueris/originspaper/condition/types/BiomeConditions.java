package io.github.dueris.originspaper.condition.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.meta.MetaConditions;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomeConditions {
	public static void registerAll() {
		MetaConditions.register(Registries.BIOME_CONDITION, BiomeConditions::register);
		register(new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("high_humidity"),
			SerializableData.serializableData(),
			(data, biome) -> {
				return biome.value().climateSettings.downfall() > 0.85F;
			}
		));
		register(new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("temperature"),
			SerializableData.serializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, biome) -> {
				return ((Comparison) data.get("comparison")).compare(biome.value().getBaseTemperature(), data.getFloat("compare_to"));
			}
		));
		register(new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("precipitation"),
			SerializableData.serializableData()
				.add("precipitation", SerializableDataTypes.enumValue(Biome.Precipitation.class)),
			(data, biome) -> {
				return biome.value().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(data.get("precipitation"));
			}
		));
		register(new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			SerializableData.serializableData()
				.add("tag", SerializableDataTypes.BIOME_TAG),
			(data, biome) -> {
				TagKey<Biome> biomeTag = data.get("tag");
				return biome.is(biomeTag);
			}
		));
	}

	public static void register(@NotNull ConditionTypeFactory<Holder<Biome>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION).register(factory, factory.getSerializerId());
	}

}
