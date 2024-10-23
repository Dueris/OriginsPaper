package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class PreventSleepPowerType extends PowerType implements Prioritized<PreventSleepPowerType>, Comparable<PreventSleepPowerType> {

	private final Predicate<BlockInWorld> blockCondition;
	private final Component message;

	private final boolean allowSpawnPoint;
	private final int priority;

	public PreventSleepPowerType(Power power, LivingEntity entity, Predicate<BlockInWorld> blockCondition, Component message, boolean allowSpawnPoint, int priority) {
		super(power, entity);
		this.blockCondition = blockCondition;
		this.message = message;
		this.allowSpawnPoint = allowSpawnPoint;
		this.priority = priority;
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

	public boolean doesPrevent(LevelReader world, BlockPos pos) {
		return blockCondition == null
			|| blockCondition.test(new BlockInWorld(world, pos, true));
	}

	public Component getMessage() {
		return message;
	}

	public boolean doesAllowSpawnPoint() {
		return allowSpawnPoint;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_sleep"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("message", ApoliDataTypes.DEFAULT_TRANSLATABLE_TEXT, Component.translatable("text.apoli.cannot_sleep"))
				.add("set_spawn_point", SerializableDataTypes.BOOLEAN, false)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new PreventSleepPowerType(power, entity,
				data.get("block_condition"),
				data.get("message"),
				data.get("set_spawn_point"),
				data.get("priority")
			)
		).allowCondition();
	}

}
