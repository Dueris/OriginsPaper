package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class OffsetCondition {

	public static @NotNull ActionFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("offset"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.BLOCK_ACTION)
				.add("x", SerializableDataTypes.INT, 0)
				.add("y", SerializableDataTypes.INT, 0)
				.add("z", SerializableDataTypes.INT, 0),
			(data, block) -> ((ActionFactory<Triple<Level, BlockPos, Direction>>) data.get("action")).accept(Triple.of(
				block.getLeft(),
				block.getMiddle().offset(data.getInt("x"), data.getInt("y"), data.getInt("z")),
				block.getRight())
			)
		);
	}
}
