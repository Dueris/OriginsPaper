package me.dueris.originspaper.factory.condition.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.factory.condition.meta.MetaConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomeConditions {
	public static void registerAll() {
		MetaConditions.register(me.dueris.originspaper.registry.Registries.BIOME_CONDITION, BiomeConditions::register);
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("high_humidity"),
			InstanceDefiner.instanceDefiner(),
			(data, biome) -> {
				return biome.value().climateSettings.downfall() > 0.85F;
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("temperature"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, biome) -> {
				return ((Comparison) data.get("comparison")).compare(biome.value().getBaseTemperature(), data.getFloat("compare_to"));
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("precipitation"),
			InstanceDefiner.instanceDefiner()
				.add("precipitation", SerializableDataTypes.enumValue(Biome.Precipitation.class)),
			(data, biome) -> {
				return biome.value().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(data.get("precipitation"));
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			InstanceDefiner.instanceDefiner()
				.add("tag", SerializableDataTypes.BIOME_TAG),
			(data, biome) -> {
				TagKey<Biome> biomeTag = data.get("tag");
				return biome.is(biomeTag);
			}
		));
	}

	public static void register(@NotNull ConditionFactory<Holder<Biome>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(me.dueris.originspaper.registry.Registries.BIOME_CONDITION).register(factory, factory.getSerializerId());
	}

}
