package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.SideMetaActionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SideBlockActionType extends BlockActionType implements SideMetaActionType<BlockActionContext, BlockAction> {

	private final BlockAction action;
	private final Side side;

	public SideBlockActionType(BlockAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeAction(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.SIDE;
	}

	@Override
	public BlockAction action() {
		return action;
	}

	@Override
	public Side side() {
		return side;
	}

}
