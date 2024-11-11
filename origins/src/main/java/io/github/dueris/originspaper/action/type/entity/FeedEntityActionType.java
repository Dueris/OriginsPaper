package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FeedEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<FeedEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("nutrition", SerializableDataTypes.INT)
            .add("saturation", SerializableDataTypes.FLOAT),
        data -> new FeedEntityActionType(
            data.get("nutrition"),
            data.get("saturation")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("nutrition", actionType.nutrition)
            .set("saturation", actionType.saturation)
    );

    private final int nutrition;
    private final float saturation;

    public FeedEntityActionType(int nutrition, float saturation) {
        this.nutrition = nutrition;
        this.saturation = saturation;
    }

    @Override
    protected void execute(Entity entity) {

        if (entity instanceof Player player) {
            player.getFoodData().eat(nutrition, saturation);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.FEED;
    }

}
