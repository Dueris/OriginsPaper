package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SetBlockBlockActionType extends BlockActionType {

    public static final TypedDataObjectFactory<SetBlockBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block", SerializableDataTypes.BLOCK_STATE),
        data -> new SetBlockBlockActionType(
            data.get("block")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("block", actionType.blockState)
    );

    private final BlockState blockState;

    public SetBlockBlockActionType(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
        world.setBlockAndUpdate(pos, blockState);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BlockActionTypes.SET_BLOCK;
    }

}
