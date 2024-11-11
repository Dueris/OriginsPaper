package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChoiceMetaActionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChoiceBlockActionType extends BlockActionType implements ChoiceMetaActionType<BlockActionContext, BlockAction> {

	private final ShufflingList<BlockAction> actions;

	public ChoiceBlockActionType(ShufflingList<BlockAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeActions(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.CHOICE;
	}

	@Override
	public ShufflingList<BlockAction> actions() {
		return actions;
	}

}
