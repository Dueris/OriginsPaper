package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class OffsetActionType {

	public static void action(Level world, @NotNull BlockPos pos, Direction direction, @NotNull Consumer<Triple<Level, BlockPos, Direction>> action, Vec3i offset) {
		action.accept(Triple.of(world, pos.offset(offset), direction));
	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("offset"),
			new SerializableData()
				.add("action", ApoliDataTypes.BLOCK_ACTION)
				.add("x", SerializableDataTypes.INT, 0)
				.add("y", SerializableDataTypes.INT, 0)
				.add("z", SerializableDataTypes.INT, 0),
			(data, block) -> action(block.getLeft(), block.getMiddle(), block.getRight(),
				data.get("action"),
				new Vec3i(data.get("x"), data.get("y"), data.get("z"))
			)
		);
	}

}
