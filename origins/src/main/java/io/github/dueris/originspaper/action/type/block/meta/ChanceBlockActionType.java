package io.github.dueris.originspaper.action.type.block.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.context.BlockActionContext;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChanceMetaActionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChanceBlockActionType extends BlockActionType implements ChanceMetaActionType<BlockActionContext, BlockAction> {

	private final BlockAction successAction;
	private final Optional<BlockAction> failAction;

	private final float chance;

	public ChanceBlockActionType(BlockAction successAction, Optional<BlockAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
		executeAction(new BlockActionContext(world, pos, direction));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.CHANCE;
	}

	@Override
	public BlockAction successAction() {
		return successAction;
	}

	@Override
	public Optional<BlockAction> failAction() {
		return failAction;
	}

	@Override
	public float chance() {
		return chance;
	}

}
