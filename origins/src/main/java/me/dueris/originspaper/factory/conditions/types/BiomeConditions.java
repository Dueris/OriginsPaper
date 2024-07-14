package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;

import java.util.function.BiPredicate;

public class BiomeConditions {

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("high_humidity"), (data, biome) -> {
			return biome.value().climateSettings.downfall() > 0.85F;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("temperature"), (data, biome) -> {
			return Comparison.fromString(data.getString("comparison")).compare(biome.value().getBaseTemperature(), data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("category"), (data, biome) -> {
			ResourceLocation tagId = OriginsPaper.apoliIdentifier("category/" + data.getString("category"));
			TagKey<Biome> biomeTag = TagKey.create(net.minecraft.core.registries.Registries.BIOME, tagId);
			return biome.is(biomeTag);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("precipitation"), (data, biome) -> {
			Precipitation precipitation = data.getEnumValue("precipitation", Precipitation.class);
			return biome.value().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(precipitation);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, biome) -> {
			ResourceLocation tagId = data.getResourceLocation("tag");
			TagKey<Biome> biomeTag = TagKey.create(net.minecraft.core.registries.Registries.BIOME, tagId);
			return biome.is(biomeTag);
		}));
	}

	private net.minecraft.world.level.biome.Biome.Precipitation getPrecipitation(FactoryJsonObject condition) {
		String lowerCase = condition.getString("precipitation").toLowerCase();
		switch (lowerCase) {
			case "none" -> {
				return Precipitation.NONE;
			}
			case "snow" -> {
				return Precipitation.SNOW;
			}
			case "rain" -> {
				return Precipitation.RAIN;
			}
			default -> {
				return null;
			}
		}
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIOME_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, Holder<Biome>> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, Holder<Biome>> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, Holder<Biome> tester) {
			return test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}
}
