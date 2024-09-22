package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
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

public class AreaOfEffectActionType {

	public static void action(Level world, BlockPos pos, Direction direction, Consumer<Triple<Level, BlockPos, Direction>> blockAction, Predicate<BlockInWorld> blockCondition, Shape shape, int radius) {

		for (BlockPos collectedPos : Shape.getPositions(pos, shape, radius)) {

			if (blockCondition.test(new BlockInWorld(world, collectedPos, true))) {
				blockAction.accept(Triple.of(world, collectedPos, direction));
			}

		}

	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("area_of_effect"),
			new SerializableData()
				.add("block_action", ApoliDataTypes.BLOCK_ACTION)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
				.add("radius", SerializableDataTypes.INT, 16),
			(data, block) -> action(block.getLeft(), block.getMiddle(), block.getRight(),
				data.get("block_action"),
				data.getOrElse("block_condition", cachedBlock -> true),
				data.get("shape"),
				data.get("radius")
			)
		);
	}

}
