package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBlockBreakPowerType extends PowerType {

	public static final TypedDataObjectFactory<ActionOnBlockBreakPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("only_when_harvested", SerializableDataTypes.BOOLEAN, false),
		(data, condition) -> new ActionOnBlockBreakPowerType(
			data.get("entity_action"),
			data.get("block_action"),
			data.get("block_condition"),
			data.get("only_when_harvested"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("block_action", powerType.blockAction)
			.set("block_condition", powerType.blockCondition)
			.set("only_when_harvested", powerType.onlyWhenHarvested)
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<BlockAction> blockAction;

	private final Optional<BlockCondition> blockCondition;
	private final boolean onlyWhenHarvested;

	public ActionOnBlockBreakPowerType(Optional<EntityAction> entityAction, Optional<BlockAction> blockAction, Optional<BlockCondition> blockCondition, boolean onlyWhenHarvested, Optional<EntityCondition> condition) {
		super(condition);
		this.blockCondition = blockCondition;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.onlyWhenHarvested = onlyWhenHarvested;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_BLOCK_BREAK;
	}

	public boolean doesApply(SavedBlockPosition savedBlock) {
		return blockCondition
			.map(condition -> condition.test((Level) savedBlock.getLevel(), savedBlock.getPos()))
			.orElse(true);
	}

	public void executeActions(boolean successfulHarvest, BlockPos pos, Direction direction) {

		if (!successfulHarvest && onlyWhenHarvested) {
			return;
		}

		blockAction.ifPresent(action -> action.execute(getHolder().level(), pos, Optional.of(direction)));
		entityAction.ifPresent(action -> action.execute(getHolder()));

	}

}
