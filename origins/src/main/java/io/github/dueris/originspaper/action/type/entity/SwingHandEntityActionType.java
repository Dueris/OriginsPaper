package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SwingHandEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<SwingHandEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("hand", SerializableDataTypes.HAND, InteractionHand.MAIN_HAND),
        data -> new SwingHandEntityActionType(
            data.get("hand")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("hand", actionType.hand)
    );

    private final InteractionHand hand;

    public SwingHandEntityActionType(InteractionHand hand) {
        this.hand = hand;
    }

    @Override
    protected void execute(Entity entity) {

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.swing(hand, true);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.SWING_HAND;
    }

}
