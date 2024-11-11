package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.DelayMetaActionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DelayBlockActionType extends BlockActionType implements DelayMetaActionType<BlockActionContext, BlockAction> {

	private final BlockAction action;
	private final int ticks;

	public DelayBlockActionType(BlockAction action, int ticks) {
		this.action = action;
		this.ticks = ticks;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeAction(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.DELAY;
	}

	@Override
	public BlockAction action() {
		return action;
	}

	@Override
	public int ticks() {
		return ticks;
	}

}
