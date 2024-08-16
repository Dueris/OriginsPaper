package io.github.dueris.originspaper.action.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.action.Actions;
import io.github.dueris.originspaper.action.meta.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;

public class BlockActions {

	public static void register(ActionFactory<Triple<Level, BlockPos, Direction>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.BLOCK_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.BLOCK_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.BLOCK_ACTION, ApoliDataTypes.BLOCK_CONDITION,
				t -> new BlockInWorld(t.getLeft(), t.getMiddle(), true)));
		register(ChoiceAction.getFactory(ApoliDataTypes.BLOCK_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.BLOCK_ACTION, ApoliDataTypes.BLOCK_CONDITION,
				t -> new BlockInWorld(t.getLeft(), t.getMiddle(), true)));
		register(DelayAction.getFactory(ApoliDataTypes.BLOCK_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.BLOCK_ACTION, block -> !block.getLeft().isClientSide));

		Actions.registerPackage(BlockActions::register, "io.github.dueris.originspaper.action.types.block");
	}

}
