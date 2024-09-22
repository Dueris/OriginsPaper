package io.github.dueris.originspaper.action.factory;

import io.github.dueris.originspaper.action.type.block.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class BlockActions {

	public static void register() {

		MetaActions.register(ApoliDataTypes.BLOCK_ACTION, ApoliDataTypes.BLOCK_CONDITION, block -> new BlockInWorld(block.getLeft(), block.getMiddle(), true), BlockActions::register);

		register(OffsetActionType.getFactory());
		register(SetBlockActionType.getFactory());
		register(AddBlockActionType.getFactory());
		register(ExecuteCommandActionType.getFactory());
		register(BoneMealActionType.getFactory());
		register(ModifyBlockStateActionType.getFactory());
		register(ExplodeActionType.getFactory());
		register(AreaOfEffectActionType.getFactory());
		register(SpawnEntityActionType.getFactory());
	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> register(ActionTypeFactory<Triple<Level, BlockPos, Direction>> actionFactory) {
		return Registry.register(ApoliRegistries.BLOCK_ACTION, actionFactory.getSerializerId(), actionFactory);
	}

}
