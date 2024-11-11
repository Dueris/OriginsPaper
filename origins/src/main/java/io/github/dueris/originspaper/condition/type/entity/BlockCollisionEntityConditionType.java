package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.access.BlockCollisionSpliteratorAccess;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockCollisionEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<BlockCollisionEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
            .add("offset_x", SerializableDataTypes.DOUBLE, 0.0)
            .add("offset_y", SerializableDataTypes.DOUBLE, 0.0)
            .add("offset_z", SerializableDataTypes.DOUBLE, 0.0),
        data -> new BlockCollisionEntityConditionType(
            data.get("block_collision"),
            new Vec3(
                data.get("offset_x"),
                data.get("offset_y"),
                data.get("offset_z")
            )
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("block_condition", conditionType.blockCondition)
            .set("offset_x", conditionType.offset.x())
            .set("offset_y", conditionType.offset.y())
            .set("offset_z", conditionType.offset.z())
    );

    private final Optional<BlockCondition> blockCondition;
    private final Vec3 offset;

    public BlockCollisionEntityConditionType(Optional<BlockCondition> blockCondition, Vec3 offset) {
        this.blockCondition = blockCondition;
        this.offset = offset;
    }

    @Override
    public boolean test(Entity entity) {

        AABB boundingBox = entity.getBoundingBox().move(offset);
        Level world = entity.level();

        BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(world, entity, boundingBox, false, (pos, shape) -> pos);
        ((BlockCollisionSpliteratorAccess) spliterator).apoli$setGetOriginalShapes(true);

        while (spliterator.hasNext()) {

            BlockPos pos = spliterator.next();

            if (blockCondition.map(condition -> condition.test(world, pos)).orElse(true)) {
                return true;
            }

        }

        return false;

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.BLOCK_COLLISION;
    }

}
