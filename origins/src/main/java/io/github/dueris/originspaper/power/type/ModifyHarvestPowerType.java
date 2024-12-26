package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyHarvestPowerType extends PowerType implements Prioritized<ModifyHarvestPowerType>, Comparable<ModifyHarvestPowerType> {

	public static final TypedDataObjectFactory<ModifyHarvestPowerType> DATA_FACTORY = createConditionedDataFactory(
		new SerializableData()
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("allow", SerializableDataTypes.BOOLEAN, true)
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new ModifyHarvestPowerType(
			data.get("block_condition"),
			data.get("allow"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("block_condition", powerType.blockCondition)
			.set("allow", powerType.allow)
			.set("priority", powerType.getPriority())
	);

	private final Optional<BlockCondition> blockCondition;

	private final boolean allow;
	private final int priority;

	public ModifyHarvestPowerType(Optional<BlockCondition> blockCondition, boolean allow, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.blockCondition = blockCondition;
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_HARVEST;
	}

	@Override
	public int compareTo(@NotNull ModifyHarvestPowerType other) {
		int priorityResult = Integer.compare(this.getPriority(), other.getPriority());
		return priorityResult != 0
			? priorityResult
			: Boolean.compare(this.isAllowed(), other.isAllowed());
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(BlockGetter blockView, BlockPos pos) {
		return blockView instanceof Level world && blockCondition
			.map(condition -> condition.test(world, pos))
			.orElse(true);
	}

	public boolean doesApply(SavedBlockPosition savedBlockPosition) {
		return blockCondition
			.map(condition -> condition.test(savedBlockPosition))
			.orElse(true);
	}

	public boolean isAllowed() {
		return allow;
	}

}
