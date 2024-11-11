package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class EmitGameEventEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<EmitGameEventEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("event", SerializableDataTypes.GAME_EVENT_ENTRY),
        data -> new EmitGameEventEntityActionType(
            data.get("event")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("event", actionType.event)
    );

    private final Holder<GameEvent> event;

    public EmitGameEventEntityActionType(Holder<GameEvent> event) {
        this.event = event;
    }

    @Override
    protected void execute(Entity entity) {
        entity.gameEvent(event);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.EMIT_GAME_EVENT;
    }

}
