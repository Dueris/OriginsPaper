package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class ModifyHarvestPower extends PowerType {
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final boolean allow;
	private final int priority;

	public ModifyHarvestPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  ConditionTypeFactory<BlockInWorld> blockCondition, boolean allow, int priority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.blockCondition = blockCondition;
		this.allow = allow;
		this.priority = priority;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_harvest"), PowerType.getFactory().getSerializableData()
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("allow", SerializableDataTypes.BOOLEAN)
			.add("priority", SerializableDataTypes.INT, 0));
	}

	public int compareTo(@NotNull ModifyHarvestPower other) {
		int priorityResult = Integer.compare(this.priority, other.priority);
		return priorityResult != 0 ? priorityResult : Boolean.compare(this.isHarvestAllowed(), other.isHarvestAllowed());
	}

	public boolean doesApply(BlockPos pos, @NotNull Entity entity) {
		return doesApply(new BlockInWorld(entity.level(), pos, true));
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public boolean isHarvestAllowed() {
		return allow;
	}
}
