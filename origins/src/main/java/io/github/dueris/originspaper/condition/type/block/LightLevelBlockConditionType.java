package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LightLevelBlockConditionType extends BlockConditionType {

    public static final TypedDataObjectFactory<LightLevelBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("light_type", SerializableDataType.enumValue(LightLayer.class).optional(), Optional.empty())
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT),
        data -> new LightLevelBlockConditionType(
            data.get("light_type"),
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("light_type", conditionType.lightType)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final Optional<LightLayer> lightType;

    private final Comparison comparison;
    private final int compareTo;

    public LightLevelBlockConditionType(Optional<LightLayer> lightType, Comparison comparison, int compareTo) {
        this.lightType = lightType;
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {

        int lightLevel = lightType
            .map(_lightType -> world.getBrightness(_lightType, pos))
            .orElseGet(() -> world.getMaxLocalRawBrightness(pos));

        return comparison.compare(lightLevel, compareTo);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BlockConditionTypes.LIGHT_LEVEL;
    }

}
