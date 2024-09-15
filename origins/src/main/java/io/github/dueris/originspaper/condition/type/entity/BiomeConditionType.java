package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class BiomeConditionType {

	public static boolean condition(Entity entity, Predicate<Tuple<BlockPos, Holder<Biome>>> biomeCondition, Collection<ResourceKey<Biome>> specifiedBiomeKeys) {

		Holder<Biome> biomeEntry = entity.level().getBiome(entity.blockPosition());
		ResourceKey<Biome> biomeKey = biomeEntry.unwrapKey().orElseThrow();

		return (specifiedBiomeKeys.isEmpty() || specifiedBiomeKeys.contains(biomeKey))
			&& biomeCondition.test(new Tuple<>(entity.blockPosition(), biomeEntry));

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("biome"),
			new SerializableData()
				.add("condition", ApoliDataTypes.BIOME_CONDITION, null)
				.add("biome", SerializableDataTypes.registryKey(Registries.BIOME), null)
				.add("biomes", SerializableDataType.of(SerializableDataTypes.registryKey(Registries.BIOME).listOf()), null),
			(data, entity) -> {

				Set<ResourceKey<Biome>> specifiedBiomeKeys = new HashSet<>();

				data.ifPresent("biome", specifiedBiomeKeys::add);
				data.ifPresent("biomes", specifiedBiomeKeys::addAll);

				return condition(entity,
					data.getOrElse("condition", posAndBiome -> true),
					specifiedBiomeKeys
				);

			}
		);
	}

}
