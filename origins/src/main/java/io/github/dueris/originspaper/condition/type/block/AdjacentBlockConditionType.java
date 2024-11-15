package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AdjacentBlockConditionType extends BlockConditionType {

	public static final TypedDataObjectFactory<AdjacentBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("adjacent_condition", BlockCondition.DATA_TYPE)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.INT),
		data -> new AdjacentBlockConditionType(
			data.get("adjacent_condition"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("adjacent_condition", conditionType.adjacentCondition)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final BlockCondition adjacentCondition;
	private final Comparison comparison;

	private final int compareTo;

	public AdjacentBlockConditionType(BlockCondition adjacentCondition, Comparison comparison, int compareTo) {
		this.adjacentCondition = adjacentCondition;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {

		int matches = 0;

		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = pos.relative(direction);

			if (world.hasChunkAt(offsetPos) && adjacentCondition.test(world, offsetPos)) {
				matches++;
			}

		}

		return comparison.compare(matches, compareTo);

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BlockConditionTypes.ADJACENT;
	}

}
