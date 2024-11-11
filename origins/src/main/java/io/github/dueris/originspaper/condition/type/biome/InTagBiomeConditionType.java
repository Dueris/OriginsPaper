package io.github.dueris.originspaper.condition.type.biome;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiomeConditionType;
import io.github.dueris.originspaper.condition.type.BiomeConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class InTagBiomeConditionType extends BiomeConditionType {

    public static final TypedDataObjectFactory<InTagBiomeConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("tag", SerializableDataTypes.BIOME_TAG),
        data -> new InTagBiomeConditionType(
            data.get("tag")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("tag", conditionType.tag)
    );

    private final TagKey<Biome> tag;

    public InTagBiomeConditionType(TagKey<Biome> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockPos pos, Holder<Biome> biomeEntry) {
        return biomeEntry.is(tag);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BiomeConditionTypes.IN_TAG;
    }

}
