package io.github.dueris.originspaper.action;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.entity.meta.AndEntityActionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;

public final class EntityAction extends AbstractAction<EntityActionContext, EntityActionType> {

	public static final SerializableDataType<EntityAction> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.actions("type", EntityActionTypes.DATA_TYPE, AndEntityActionType::new, EntityAction::new));

	public EntityAction(EntityActionType actionType) {
		super(actionType);
	}

	public void execute(Entity entity) {
		accept(new EntityActionContext(entity));
	}

}
