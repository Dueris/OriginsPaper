package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;

public class BlockActionAtActionType {

	public static void action(Entity entity, Consumer<Triple<Level, BlockPos, Direction>> blockAction) {
		blockAction.accept(Triple.of(entity.level(), entity.blockPosition(), Direction.UP));
	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("block_action_at"),
			new SerializableData()
				.add("block_action", ApoliDataTypes.BLOCK_ACTION),
			(data, entity) -> action(entity,
				data.get("block_action")
			)
		);
	}

}
