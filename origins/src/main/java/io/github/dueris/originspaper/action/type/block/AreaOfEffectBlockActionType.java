package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class AreaOfEffectBlockActionType extends BlockActionType {

	public static final TypedDataObjectFactory<AreaOfEffectBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("block_action", BlockAction.DATA_TYPE)
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
			.add("radius", SerializableDataTypes.NON_NEGATIVE_INT, 16),
		data -> new AreaOfEffectBlockActionType(
			data.get("block_action"),
			data.get("block_condition"),
			data.get("shape"),
			data.get("radius")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("block_action", actionType.blockAction)
			.set("block_condition", actionType.blockCondition)
			.set("shape", actionType.shape)
			.set("radius", actionType.radius)
	);

	private final BlockAction blockAction;
	private final Optional<BlockCondition> blockCondition;

	private final Shape shape;
	private final int radius;

	public AreaOfEffectBlockActionType(BlockAction blockAction, Optional<BlockCondition> blockCondition, Shape shape, int radius) {
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
		this.shape = shape;
		this.radius = radius;
	}

	@Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {

		Collection<BlockPos> affectedPositions = Shape.getPositions(pos, shape, radius);

		for (BlockPos affectedPosition : affectedPositions) {

			if (blockCondition.map(condition -> condition.test(world, affectedPosition)).orElse(true)) {
				blockAction.execute(world, affectedPosition, direction);
			}

		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BlockActionTypes.AREA_OF_EFFECT;
	}

}
