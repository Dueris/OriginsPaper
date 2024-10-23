package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ModifyHarvestPowerType extends PowerType implements Prioritized<ModifyHarvestPowerType>, Comparable<ModifyHarvestPowerType> {

	private final Predicate<BlockInWorld> blockCondition;

	private final boolean allow;
	private final int priority;

	public ModifyHarvestPowerType(Power power, LivingEntity entity, Predicate<BlockInWorld> predicate, boolean allow, int priority) {
		super(power, entity);
		this.blockCondition = predicate;
		this.allow = allow;
		this.priority = priority;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_harvest"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("allow", SerializableDataTypes.BOOLEAN)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ModifyHarvestPowerType(power, entity,
				data.get("block_condition"),
				data.get("allow"),
				data.get("priority")
			)
		).allowCondition();
	}

	@Override
	public int compareTo(@NotNull ModifyHarvestPowerType other) {
		int priorityResult = Integer.compare(this.getPriority(), other.getPriority());
		return priorityResult != 0 ? priorityResult : Boolean.compare(this.isHarvestAllowed(), other.isHarvestAllowed());
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(BlockPos pos) {
		return doesApply(new BlockInWorld(entity.level(), pos, true));
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public boolean isHarvestAllowed() {
		return allow;
	}

}
