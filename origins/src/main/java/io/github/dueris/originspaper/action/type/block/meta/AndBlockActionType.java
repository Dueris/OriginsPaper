package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.AndMetaActionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AndBlockActionType extends BlockActionType implements AndMetaActionType<BlockActionContext, BlockAction> {

	private final List<BlockAction> actions;

	public AndBlockActionType(List<BlockAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeActions(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.AND;
	}

	@Override
	public List<BlockAction> actions() {
		return actions;
	}

}
