package me.dueris.originspaper.factory.conditions.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomeConditions {
	public static void registerAll() {
		MetaConditions.register(me.dueris.originspaper.registry.Registries.BIOME_CONDITION, BiomeConditions::register);
		/*register(new ConditionFactory(OriginsPaper.apoliIdentifier("high_humidity"), (data, biome) -> biome.value().climateSettings.downfall() > 0.85F));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("temperature"), (data, biome) -> Comparison.fromString(data.getString("comparison")).compare(biome.value().getBaseTemperature(), data.getNumber("compare_to").getFloat())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("category"), (data, biome) -> {
			ResourceLocation tagId = OriginsPaper.apoliIdentifier("category/" + data.getString("category"));
			TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, tagId);
			return biome.is(biomeTag);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("precipitation"), (data, biome) -> {
			Precipitation precipitation = data.getEnumValue("precipitation", Precipitation.class);
			return biome.value().getPrecipitationAt(new BlockPos(0, 64, 0)).equals(precipitation);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, biome) -> {
			ResourceLocation tagId = data.getResourceLocation("tag");
			TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, tagId);
			return biome.is(biomeTag);
		}));*/
	}

	public static void register(@NotNull ConditionFactory<Holder<Biome>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(me.dueris.originspaper.registry.Registries.BIOME_CONDITION).register(factory, factory.getSerializerId());
	}

}
