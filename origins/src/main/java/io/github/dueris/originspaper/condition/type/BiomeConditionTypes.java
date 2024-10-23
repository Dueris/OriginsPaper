package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.biome.InTagConditionType;
import io.github.dueris.originspaper.condition.type.biome.PrecipitationConditionType;
import io.github.dueris.originspaper.condition.type.biome.TemperatureConditionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

public class BiomeConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.BIOME_CONDITION, BiomeConditionTypes::register);
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("high_humidity"), biomeEntry -> biomeEntry.value().climateSettings.downfall() > 0.85F));
		register(TemperatureConditionType.getFactory());
		register(PrecipitationConditionType.getFactory());
		register(InTagConditionType.getFactory());
	}

	public static ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>> createSimpleFactory(ResourceLocation id, Predicate<Holder<Biome>> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, posAndBiome) -> condition.test(posAndBiome.getB()));
	}

	public static <F extends ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.BIOME_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
