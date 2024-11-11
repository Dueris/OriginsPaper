package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockStateBlockConditionType extends BlockConditionType {

    public static final TypedDataObjectFactory<BlockStateBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("property", SerializableDataTypes.STRING)
            .add("comparison", ApoliDataTypes.COMPARISON, null)
            .add("compare_to", SerializableDataTypes.INT, null)
            .add("value", SerializableDataTypes.BOOLEAN, null)
            .add("enum", SerializableDataTypes.STRING, null),
        data -> new BlockStateBlockConditionType(
            data.get("property"),
            data.get("comparison"),
            data.get("compare_to"),
            data.get("value"),
            data.get("enum")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("property", conditionType.property)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
            .set("value", conditionType.boolValue)
            .set("enum", conditionType.enumValue)
    );

    private final String property;

    private final Comparison comparison;
    private final Integer compareTo;

    private final Boolean boolValue;
    private final String enumValue;

    public BlockStateBlockConditionType(String property, Comparison comparison, Integer compareTo, Boolean boolValue, String enumValue) {
        this.property = property;
        this.comparison = comparison;
        this.compareTo = compareTo;
        this.boolValue = boolValue;
        this.enumValue = enumValue;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {

        var propertyValue = blockState.getProperties()
            .stream()
            .filter(prop -> prop.getName().equals(property))
            .map(blockState::getValue)
            .findFirst()
            .orElse(null);

        return switch (propertyValue) {
            case Enum<?> enumProp when enumValue != null ->
                enumProp.name().equalsIgnoreCase(enumValue);
            case Boolean boolProp when boolValue != null ->
                boolProp == boolValue;
            case Integer intProp when comparison != null && compareTo != null ->
                comparison.compare(intProp, compareTo);
            case null, default ->
                propertyValue != null;
        };

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BlockConditionTypes.BLOCK_STATE;
    }

}
