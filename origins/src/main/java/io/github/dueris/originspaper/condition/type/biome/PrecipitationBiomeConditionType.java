package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class PrecipitationBiomeConditionType extends BiomeConditionType {

    public static final TypedDataObjectFactory<PrecipitationBiomeConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("precipitation", SerializableDataType.enumValue(Biome.Precipitation.class)),
        data -> new PrecipitationBiomeConditionType(
            data.get("precipitation")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("precipitation", conditionType.precipitation)
    );

    private final Biome.Precipitation precipitation;

    public PrecipitationBiomeConditionType(Biome.Precipitation precipitation) {
        this.precipitation = precipitation;
    }

    @Override
    public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
        return biomeEntry.value().getPrecipitationAt(pos) == precipitation;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BiomeConditionTypes.PRECIPITATION;
    }

}
