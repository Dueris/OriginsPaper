package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OffsetBlockActionType extends BlockActionType {

    public static final TypedDataObjectFactory<OffsetBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("action", BlockAction.DATA_TYPE)
            .add("x", SerializableDataTypes.INT, 0)
            .add("y", SerializableDataTypes.INT, 0)
            .add("z", SerializableDataTypes.INT, 0),
        data -> new OffsetBlockActionType(
            data.get("action"),
            new Vec3i(
                data.get("x"),
                data.get("y"),
                data.get("z")
            )
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("action", actionType.blockAction)
            .set("x", actionType.offset.getX())
            .set("y", actionType.offset.getY())
            .set("z", actionType.offset.getZ())
    );

    private final BlockAction blockAction;
    private final Vec3i offset;

    public OffsetBlockActionType(BlockAction blockAction, Vec3i offset) {
        this.blockAction = blockAction;
        this.offset = offset;
    }

    @Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
        blockAction.execute(world, pos.offset(offset), direction);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BlockActionTypes.OFFSET;
    }

}
