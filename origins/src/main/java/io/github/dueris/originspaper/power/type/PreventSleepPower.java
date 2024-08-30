package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class PreventSleepPower extends PowerType {
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final Component message;
	private final boolean setSpawnPoint;

	public PreventSleepPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							 ConditionTypeFactory<BlockInWorld> blockCondition, Component message, boolean setSpawnPoint) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.blockCondition = blockCondition;
		this.message = message;
		this.setSpawnPoint = setSpawnPoint;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_sleep"))
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("message", ApoliDataTypes.DEFAULT_TRANSLATABLE_TEXT, Component.literal("You cannot sleep"))
			.add("set_spawn_point", SerializableDataTypes.BOOLEAN, false);
	}

	public boolean doesPrevent(LevelReader world, BlockPos pos) {
		return blockCondition == null
			|| blockCondition.test(new BlockInWorld(world, pos, true));
	}

	public Component getMessage() {
		return message;
	}

	public boolean doesAllowSpawnPoint() {
		return setSpawnPoint;
	}

}
