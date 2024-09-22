package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ActionOnWakeUpPower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;
	private final ActionTypeFactory<Triple<Level, BlockPos, Direction>> blockAction;
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final List<org.bukkit.entity.Player> tickedAlready = new LinkedList<>();

	public ActionOnWakeUpPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							   ActionTypeFactory<Entity> entityAction, ActionTypeFactory<Triple<Level, BlockPos, Direction>> blockAction, ConditionTypeFactory<BlockInWorld> blockCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_on_wake_up"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null));
	}

	public boolean doesApply(BlockPos pos, @NotNull Entity entity) {
		BlockInWorld cbp = new BlockInWorld(entity.level(), pos, true);
		return doesApply(cbp) && isActive(entity);
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public void executeActions(BlockPos pos, Direction direction, Entity entity) {

		if (blockAction != null) {
			blockAction.accept(Triple.of(entity.level(), pos, direction));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}
}
