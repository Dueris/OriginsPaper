package io.github.dueris.originspaper.condition.type.block.meta;

import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OffsetBlockConditionType extends BlockConditionType {

    public static final TypedDataObjectFactory<OffsetBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("condition", BlockCondition.DATA_TYPE)
            .add("x", SerializableDataTypes.INT, 0)
            .add("y", SerializableDataTypes.INT, 0)
            .add("z", SerializableDataTypes.INT, 0)
            .addFunctionedDefault("offset", SerializableDataTypes.VECTOR, data -> new Vec3(data.get("x"), data.get("y"), data.get("z"))),
        data -> new OffsetBlockConditionType(
            data.get("condition"),
            data.get("offset")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("condition", conditionType.blockCondition)
            .set("offset", conditionType.offset)
    );

    private final BlockCondition blockCondition;
    private final Vec3i offset;

    public OffsetBlockConditionType(BlockCondition blockCondition, Vec3i offset) {
        this.blockCondition = blockCondition;
        this.offset = offset;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {

        BlockConditionContext context = offset.equals(Vec3i.ZERO)
            ? new BlockConditionContext(world, pos, blockState, blockEntity)
            : new BlockConditionContext(world, pos.offset(offset));

        return blockCondition.test(context);

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BlockConditionTypes.OFFSET;
    }

}
