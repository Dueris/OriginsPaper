package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class PreventSleepPowerType extends PowerType implements Prioritized<PreventSleepPowerType>, Comparable<PreventSleepPowerType> {

	public static final TypedDataObjectFactory<PreventSleepPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("message", ApoliDataTypes.DEFAULT_TRANSLATABLE_TEXT, Component.translatable("text.apoli.cannot_sleep"))
			.add("set_spawn_point", SerializableDataTypes.BOOLEAN, true)
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new PreventSleepPowerType(
			data.get("block_condition"),
			data.get("message"),
			data.get("set_spawn_point"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("block_condition", powerType.blockCondition)
			.set("message", powerType.getMessage())
			.set("set_spawn_point", powerType.doesAllowSpawnPoint())
			.set("priority", powerType.getPriority())
	);

	private final Optional<BlockCondition> blockCondition;
	private final Component message;

	private final boolean allowSpawnPoint;
	private final int priority;

	public PreventSleepPowerType(Optional<BlockCondition> blockCondition, Component message, boolean allowSpawnPoint, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.blockCondition = blockCondition;
		this.message = message;
		this.allowSpawnPoint = allowSpawnPoint;
		this.priority = priority;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_SLEEP;
	}

	@Override
	public int compareTo(@NotNull PreventSleepPowerType other) {
		int cmp = Boolean.compare(this.doesAllowSpawnPoint(), other.doesAllowSpawnPoint());
		return cmp != 0 ? cmp : Integer.compare(this.getPriority(), other.getPriority());
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesPrevent(LevelReader worldView, BlockPos pos) {
		return worldView instanceof Level world && blockCondition
			.map(condition -> condition.test(world, pos))
			.orElse(true);
	}

	public Component getMessage() {
		return message;
	}

	public boolean doesAllowSpawnPoint() {
		return allowSpawnPoint;
	}

}
