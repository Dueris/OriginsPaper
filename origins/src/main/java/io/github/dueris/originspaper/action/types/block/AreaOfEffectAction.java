package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AreaOfEffectAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Triple<Level, BlockPos, Direction> block) {

		Level world = block.getLeft();
		BlockPos blockPos = block.getMiddle();
		Direction direction = block.getRight();

		int radius = data.get("radius");

		Shape shape = data.get("shape");
		Predicate<BlockInWorld> blockCondition = data.get("block_condition");
		Consumer<Triple<Level, BlockPos, Direction>> blockAction = data.get("block_action");

		for (BlockPos collectedBlockPos : Shape.getPositions(blockPos, shape, radius)) {
			if (!(blockCondition == null || blockCondition.test(new BlockInWorld(world, collectedBlockPos, true))))
				continue;
			if (blockAction != null) blockAction.accept(Triple.of(world, collectedBlockPos, direction));
		}

	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("area_of_effect"),
			SerializableData.serializableData()
				.add("block_action", ApoliDataTypes.BLOCK_ACTION)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("radius", SerializableDataTypes.INT, 16)
				.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE),
			AreaOfEffectAction::action
		);
	}
}
