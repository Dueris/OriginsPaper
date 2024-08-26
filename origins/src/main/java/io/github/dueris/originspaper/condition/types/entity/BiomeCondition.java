package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class BiomeCondition {

	public static boolean condition(@NotNull Entity entity, @Nullable Predicate<Holder<Biome>> biomeCondition, @NotNull Collection<ResourceKey<Biome>> specBiomeKeys) {

		Holder<Biome> biomeEntry = entity.level().getBiome(entity.blockPosition());
		ResourceKey<Biome> biomeKey = biomeEntry.unwrapKey().orElseThrow();

		return (!specBiomeKeys.isEmpty() && specBiomeKeys.contains(biomeKey))
			&& (biomeCondition != null && biomeCondition.test(biomeEntry));

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("biome"),
			new SerializableData()
				.add("condition", ApoliDataTypes.BIOME_CONDITION, null)
				.add("biome", SerializableDataTypes.registryKey(Registries.BIOME), null)
				.add("biomes", SerializableDataBuilder.of(SerializableDataTypes.registryKey(Registries.BIOME).listOf()), null),
			(data, entity) -> {

				Set<ResourceKey<Biome>> specBiomeKeys = new HashSet<>();

				data.ifPresent("biome", specBiomeKeys::add);
				data.ifPresent("biomes", specBiomeKeys::addAll);

				return condition(entity,
					data.get("condition"),
					specBiomeKeys
				);

			}
		);
	}
}
