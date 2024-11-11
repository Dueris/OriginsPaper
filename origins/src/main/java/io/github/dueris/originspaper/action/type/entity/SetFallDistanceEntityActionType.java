package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetFallDistanceEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<SetFallDistanceEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("fall_distance", SerializableDataTypes.FLOAT),
        data -> new SetFallDistanceEntityActionType(
            data.get("fall_distance")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("fall_distance", actionType.fallDistance)
    );

    private final float fallDistance;

    public SetFallDistanceEntityActionType(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    @Override
    protected void execute(Entity entity) {
        entity.fallDistance = fallDistance;
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.SET_FALL_DISTANCE;
    }

}
