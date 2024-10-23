package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ExplodeActionType {

	public static void action(Level world, BlockPos pos, float power, Explosion.BlockInteraction destructionType, @Nullable Predicate<BlockInWorld> indestructibleCondition, float indestructibleResistance, @Nullable Predicate<BlockInWorld> destructibleCondition, boolean createFire) {

		if (world.isClientSide) {
			return;
		}

		if (destructibleCondition != null) {
			indestructibleCondition = Util.combineOr(destructibleCondition.negate(), indestructibleCondition);
		}

		Util.createExplosion(
			world,
			pos.getCenter(),
			power,
			createFire,
			destructionType,
			Util.getExplosionBehavior(world, indestructibleResistance, indestructibleCondition)
		);

	}

	public static ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("explode"),
			new SerializableData()
				.add("power", SerializableDataTypes.FLOAT)
				.add("destruction_type", ApoliDataTypes.DESTRUCTION_TYPE, Explosion.BlockInteraction.DESTROY)
				.add("indestructible", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("indestructible_resistance", SerializableDataTypes.FLOAT, 10.0f)
				.add("destructible", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("create_fire", SerializableDataTypes.BOOLEAN, false),
			(data, block) -> action(block.getLeft(), block.getMiddle(),
				data.get("power"),
				data.get("destruction_type"),
				data.get("indestructible"),
				data.get("indestructible_resistance"),
				data.get("destructible"),
				data.get("create_fire")
			)
		);
	}

}
