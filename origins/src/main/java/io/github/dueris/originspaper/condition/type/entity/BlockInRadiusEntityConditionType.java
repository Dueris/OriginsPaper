package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.originspaper.util.Shape;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class BlockInRadiusEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<BlockInRadiusEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block_condition", BlockCondition.DATA_TYPE)
            .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
            .add("compare_to", SerializableDataTypes.INT, 0)
            .add("radius", SerializableDataTypes.INT),
        data -> new BlockInRadiusEntityConditionType(
            data.get("block_condition"),
            data.get("shape"),
            data.get("comparison"),
            data.get("compare_to"),
            data.get("radius")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("block_condition", conditionType.blockCondition)
            .set("shape", conditionType.shape)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
            .set("radius", conditionType.radius)
    );

    private final BlockCondition blockCondition;
    private final Shape shape;

    private final Comparison comparison;
    private final int compareTo;

    private final int radius;
    private final int threshold;

    public BlockInRadiusEntityConditionType(BlockCondition blockCondition, Shape shape, Comparison comparison, int compareTo, int radius) {
        this.blockCondition = blockCondition;
        this.shape = shape;
        this.comparison = comparison;
        this.compareTo = compareTo;
        this.radius = radius;
        this.threshold = switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN ->
                compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL ->
                compareTo;
            default ->
                -1;
        };
    }

    @Override
    public boolean test(Entity entity) {

        int matches = 0;
        for (BlockPos pos : Shape.getPositions(entity.blockPosition(), shape, radius)) {

            if (blockCondition.test(entity.level(), pos)) {
                ++matches;
            }

            if (matches == threshold) {
                break;
            }

        }

        return comparison.compare(matches ,compareTo);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.BLOCK_IN_RADIUS;
    }

}
