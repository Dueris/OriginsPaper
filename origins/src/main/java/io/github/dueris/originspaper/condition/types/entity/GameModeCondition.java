package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class GameModeCondition {

	public static boolean condition(SerializableData.Instance data, Entity entity) {

		if (!(entity instanceof Player playerEntity)) {
			return false;
		}

		GameType specifiedGameMode = data.get("gamemode");
		if (playerEntity instanceof ServerPlayer serverPlayerEntity) {

			ServerPlayerGameMode interactionManager = serverPlayerEntity.gameMode;
			return interactionManager.getGameModeForPlayer() == specifiedGameMode;

		}

		return false;

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("gamemode"),
			SerializableData.serializableData()
				.add("gamemode", ApoliDataTypes.GAME_MODE),
			GameModeCondition::condition
		);
	}
}
