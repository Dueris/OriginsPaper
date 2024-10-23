package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnWakeUpPowerType extends PowerType {

	private final Predicate<BlockInWorld> blockCondition;
	private final Consumer<Entity> entityAction;
	private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;

	public ActionOnWakeUpPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Triple<Level, BlockPos, Direction>> blockAction, Predicate<BlockInWorld> blockCondition) {
		super(power, entity);
		this.blockCondition = blockCondition;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_wake_up"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
			data -> (power, entity) -> new ActionOnWakeUpPowerType(power, entity,
				data.get("entity_action"),
				data.get("block_action"),
				data.get("block_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(BlockPos pos) {
		BlockInWorld cbp = new BlockInWorld(entity.level(), pos, true);
		return doesApply(cbp);
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public void executeActions(BlockPos pos, Direction direction) {

		if (blockAction != null) {
			blockAction.accept(Triple.of(entity.level(), pos, direction));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}
}
