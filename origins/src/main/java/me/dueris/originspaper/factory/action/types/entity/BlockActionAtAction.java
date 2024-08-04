package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class BlockActionAtAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("block_action_at"),
			InstanceDefiner.instanceDefiner()
				.add("block_action", ApoliDataTypes.BLOCK_ACTION),
			(data, entity) -> ((ActionFactory<Triple<Level, BlockPos, Direction>>) data.get("block_action")).accept(
				Triple.of(entity.level(), entity.blockPosition(), Direction.UP)));
	}
}
