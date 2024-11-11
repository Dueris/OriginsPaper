package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.power.type.EntitySetPowerType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RemoveFromEntitySetBiEntityActionType extends BiEntityActionType {

    public static final TypedDataObjectFactory<RemoveFromEntitySetBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("set", ApoliDataTypes.POWER_REFERENCE),
        data -> new RemoveFromEntitySetBiEntityActionType(
            data.get("set")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("set", actionType.set)
    );

    private final PowerReference set;

    public RemoveFromEntitySetBiEntityActionType(PowerReference set) {
        this.set = set;
    }

    @Override
	protected void execute(Entity actor, Entity target) {

        if (set.getPowerTypeFrom(actor) instanceof EntitySetPowerType entitySet && entitySet.remove(target)) {
            PowerHolderComponent.syncPower(actor, set);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BiEntityActionTypes.REMOVE_FROM_ENTITY_SET;
    }

}
