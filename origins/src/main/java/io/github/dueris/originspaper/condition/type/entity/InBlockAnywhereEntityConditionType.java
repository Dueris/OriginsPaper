package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class InBlockAnywhereEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<InBlockAnywhereEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block_condition", BlockCondition.DATA_TYPE)
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
            .add("compare_to", SerializableDataTypes.INT, 0),
        data -> new InBlockAnywhereEntityConditionType(
            data.get("block_condition"),
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("block_condition", conditionType.blockCondition)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final BlockCondition blockCondition;
    private final Comparison comparison;

    private final int compareTo;
    private final int threshold;

    public InBlockAnywhereEntityConditionType(BlockCondition blockCondition, Comparison comparison, int compareTo) {
        this.blockCondition = blockCondition;
        this.comparison = comparison;
        this.compareTo = compareTo;
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

        AABB boundingBox = entity.getBoundingBox();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        BlockPos minPos = BlockPos.containing(boundingBox.minX + 0.001D, boundingBox.minY + 0.001D, boundingBox.minZ + 0.001D);
        BlockPos maxPos = BlockPos.containing(boundingBox.maxX - 0.001D, boundingBox.maxY - 0.001D, boundingBox.maxZ - 0.001D);

        int matches = 0;
        for (int x = minPos.getX(); x <= maxPos.getX() && matches < threshold; x++) {
            for (int y = minPos.getY(); y <= maxPos.getY() && matches < threshold; y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ() && matches < threshold; z++) {

                    mutablePos.set(x, y, z);

                    if (blockCondition.test(entity.level(), mutablePos)) {
                        ++matches;
                    }

                }
            }
        }

        return comparison.compare(matches, compareTo);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.IN_BLOCK_ANYWHERE;
    }

}
