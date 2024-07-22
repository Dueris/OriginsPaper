package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;

import java.util.function.BiPredicate;

public class BiomeConditions {
	public void registerConditions() {
		this.register(new ConditionFactory(OriginsPaper.apoliIdentifier("high_humidity"), (data, biome) -> biome.value().climateSettings.downfall() > 0.85F));
		this.register(new ConditionFactory(OriginsPaper.apoliIdentifier("temperature"), (data, biome) -> Comparison.fromString(data.getString("comparison")).compare(biome.value().getBaseTemperature(), data.getNumber("compare_to").getFloat())));
		this.register(new ConditionFactory(OriginsPaper.apoliIdentifier("category"), (data, biome) -> {
			ResourceLocation tagId = OriginsPaper.apoliIdentifier("category/" + data.getString("category"));
			TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, tagId);
			return biome.is(biomeTag);
		}));
		this.register(new ConditionFactory(OriginsPaper.apoliIdentifier("precipitation"), (data, biome) -> {
			Precipitation precipitation = data.getEnumValue("precipitation", Precipitation.class);
			return biome.value().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(precipitation);
		}));
		this.register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, biome) -> {
			ResourceLocation tagId = data.getResourceLocation("tag");
			TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, tagId);
			return biome.is(biomeTag);
		}));
	}

	private Precipitation getPrecipitation(FactoryJsonObject condition) {
		String lowerCase = condition.getString("precipitation").toLowerCase();
		return switch (lowerCase) {
			case "none" -> Precipitation.NONE;
			case "snow" -> Precipitation.SNOW;
			case "rain" -> Precipitation.RAIN;
			default -> null;
		};
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(me.dueris.originspaper.registry.Registries.BIOME_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, Holder<Biome>> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, Holder<Biome>> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, Holder<Biome> tester) {
			return this.test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return this.key;
		}
	}
}
