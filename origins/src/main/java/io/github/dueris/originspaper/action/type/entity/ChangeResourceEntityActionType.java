package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.PowerUtil;
import io.github.dueris.originspaper.util.ResourceOperation;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ChangeResourceEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<ChangeResourceEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
            .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD)
            .add("change", SerializableDataTypes.INT),
        data -> new ChangeResourceEntityActionType(
            data.get("resource"),
            data.get("operation"),
            data.get("change")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("resource", actionType.resource)
            .set("operation", actionType.operation)
            .set("change", actionType.change)
    );

    private final PowerReference resource;

    private final ResourceOperation operation;
    private final int change;

    public ChangeResourceEntityActionType(PowerReference resource, ResourceOperation operation, int change) {
        this.resource = resource;
        this.operation = operation;
        this.change = change;
    }

    @Override
    protected void execute(Entity entity) {

        PowerType powerType = resource.getPowerTypeFrom(entity);
        boolean modified = switch (operation) {
            case ADD ->
                PowerUtil.changeResourceValue(powerType, change);
            case SET ->
                PowerUtil.setResourceValue(powerType, change);
        };

        if (modified) {
            PowerHolderComponent.syncPower(entity, resource);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.CHANGE_RESOURCE;
    }

}
