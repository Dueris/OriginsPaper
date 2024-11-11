package io.github.dueris.originspaper.action;

import java.util.Optional;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.block.meta.AndBlockActionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class BlockAction extends AbstractAction<BlockActionContext, BlockActionType> {

	public static final SerializableDataType<BlockAction> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.actions("type", BlockActionTypes.DATA_TYPE, AndBlockActionType::new, BlockAction::new));

	public BlockAction(BlockActionType actionType) {
		super(actionType);
	}

	public void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		accept(new BlockActionContext(world, pos, direction));
	}

}
