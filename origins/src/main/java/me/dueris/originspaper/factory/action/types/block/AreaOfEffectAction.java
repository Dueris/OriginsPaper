package me.dueris.originspaper.factory.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Shape;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AreaOfEffectAction {

	public static void action(@NotNull DeserializedFactoryJson data, @NotNull Triple<Level, BlockPos, Direction> block) {

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

	public static @NotNull ActionFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("area_of_effect"),
			InstanceDefiner.instanceDefiner()
				.add("block_action", ApoliDataTypes.BLOCK_ACTION)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("radius", SerializableDataTypes.INT, 16)
				.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE),
			AreaOfEffectAction::action
		);
	}
}
