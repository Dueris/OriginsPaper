package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class GameModeConditionType {

	public static boolean condition(Entity entity, GameType gameMode) {

		if (!(entity instanceof Player player)) {
			return false;
		} else if (player instanceof ServerPlayer serverPlayer) {
			ServerPlayerGameMode interactionManager = serverPlayer.gameMode;
			return interactionManager.getGameModeForPlayer() == gameMode;
		} else {
			return false;
		}

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("gamemode"),
			new SerializableData()
				.add("gamemode", ApoliDataTypes.GAME_MODE),
			(data, entity) -> condition(entity,
				data.get("gamemode")
			)
		);
	}

}
