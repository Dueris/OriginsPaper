package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.util.Reflector;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class UsingEffectiveToolCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {

		if (!(entity instanceof Player playerEntity)) {
			return false;
		}

		if (playerEntity instanceof ServerPlayer serverPlayerEntity) {

			ServerPlayerGameMode interactionManager = serverPlayerEntity.gameMode;
			boolean isMining = Boolean.TRUE.equals(Reflector.accessField("isDestroyingBlock", ServerPlayerGameMode.class, interactionManager, Boolean.class));
			if (!isMining) {
				return false;
			}
			@Nullable BlockPos pos = Reflector.accessField("destroyPos", ServerPlayerGameMode.class, interactionManager, BlockPos.class);
			if (pos == null) return false;
			BlockState miningBlockState = entity.level().getBlockState(pos);
			return playerEntity.hasCorrectToolForDrops(miningBlockState);

		} else {
			return false;
		}

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("using_effective_tool"),
			SerializableData.serializableData(),
			UsingEffectiveToolCondition::condition
		);
	}
}
