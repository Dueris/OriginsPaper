package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class TemperatureBiomeConditionType extends BiomeConditionType {

	public static final TypedDataObjectFactory<TemperatureBiomeConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.FLOAT),
		data -> new TemperatureBiomeConditionType(
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Comparison comparison;
	private final float compareTo;

	public TemperatureBiomeConditionType(Comparison comparison, float compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
		return comparison.compare(biomeEntry.value().getBaseTemperature(), compareTo);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiomeConditionTypes.TEMPERATURE;
	}

}
