package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BiomeCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.biome.HighHumidityBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.InTagBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.PrecipitationBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.TemperatureBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.meta.AllOfBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.meta.AnyOfBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.meta.ConstantBiomeConditionType;
import io.github.dueris.originspaper.condition.type.biome.meta.RandomChanceBiomeConditionType;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.core.Registry;

public class BiomeConditionTypes {

    public static final IdentifierAlias ALIASES = new IdentifierAlias();
    public static final SerializableDataType<ConditionConfiguration<BiomeConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.BIOME_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Biome condition type \"" + id + "\" is undefined!");

    public static final ConditionConfiguration<AllOfBiomeConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(BiomeCondition.DATA_TYPE, AllOfBiomeConditionType::new));
    public static final ConditionConfiguration<AnyOfBiomeConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(BiomeCondition.DATA_TYPE, AnyOfBiomeConditionType::new));
    public static final ConditionConfiguration<ConstantBiomeConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantBiomeConditionType::new));
    public static final ConditionConfiguration<RandomChanceBiomeConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceBiomeConditionType::new));
    
    public static final ConditionConfiguration<HighHumidityBiomeConditionType> HIGH_HUMIDITY = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("high_humidity"), HighHumidityBiomeConditionType::new));
    public static final ConditionConfiguration<InTagBiomeConditionType> IN_TAG = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_tag"), InTagBiomeConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<PrecipitationBiomeConditionType> PRECIPITATION = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("precipitation"), PrecipitationBiomeConditionType.DATA_FACTORY));
    public static final ConditionConfiguration<TemperatureBiomeConditionType> TEMPERATURE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("temperature"), TemperatureBiomeConditionType.DATA_FACTORY));

    public static void register() {

    }

    @SuppressWarnings("unchecked")
	public static <T extends BiomeConditionType> ConditionConfiguration<T> register(ConditionConfiguration<T> config) {

        ConditionConfiguration<BiomeConditionType> casted = (ConditionConfiguration<BiomeConditionType>) config;
        Registry.register(ApoliRegistries.BIOME_CONDITION_TYPE, casted.id(), casted);

        return config;

    }

}
