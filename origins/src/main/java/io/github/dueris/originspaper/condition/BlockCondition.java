package io.github.dueris.originspaper.condition;

import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import io.github.dueris.calio.data.SerializableDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class BlockCondition extends AbstractCondition<BlockConditionContext, BlockConditionType> {

	public static final SerializableDataType<BlockCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", BlockConditionTypes.DATA_TYPE, BlockCondition::new));

	public BlockCondition(BlockConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public BlockCondition(BlockConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(SavedBlockPosition savedBlock) {
		return test(new BlockConditionContext(savedBlock));
	}

	public boolean test(Level world, BlockPos pos) {
		return test(new BlockConditionContext(world, pos));
	}

}
