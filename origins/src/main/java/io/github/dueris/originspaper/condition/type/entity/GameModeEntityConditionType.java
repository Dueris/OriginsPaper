package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class GameModeEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<GameModeEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("gamemode", ApoliDataTypes.GAME_MODE),
        data -> new GameModeEntityConditionType(
            data.get("gamemode")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("gamemode", conditionType.gameMode)
    );

    private final GameType gameMode;

    public GameModeEntityConditionType(GameType gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public boolean test(Entity entity) {

        if (!(entity instanceof Player player)) {
            return false;
        }

        else if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == gameMode;
        }

        else {
            return false;
        }

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.GAME_MODE;
    }

}
