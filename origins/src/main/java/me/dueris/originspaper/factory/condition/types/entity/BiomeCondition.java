package me.dueris.originspaper.factory.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BiomeCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("biome"),
			InstanceDefiner.instanceDefiner()
				.add("biome", SerializableDataTypes.IDENTIFIER, null)
				.add("biomes", SerializableDataTypes.list(SerializableDataTypes.IDENTIFIER), null)
				.add("condition", ApoliDataTypes.BIOME_CONDITION, null),
			(data, entity) -> {
				Holder<Biome> biomeEntry = entity.level().getBiome(entity.blockPosition());
				Biome biome = biomeEntry.value();
				ConditionFactory<Holder<Biome>> condition = data.get("condition");
				if (data.isPresent("biome") || data.isPresent("biomes")) {
					ResourceLocation biomeId = entity.level().registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
					if (data.isPresent("biome") && biomeId.equals(data.getId("biome"))) {
						return condition == null || condition.test(biomeEntry);
					}
					if (data.isPresent("biomes") && ((List<ResourceLocation>) data.get("biomes")).contains(biomeId)) {
						return condition == null || condition.test(biomeEntry);
					}
					return false;
				}
				return condition == null || condition.test(biomeEntry);
			}
		);
	}
}
