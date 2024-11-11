package io.github.dueris.originspaper.action.type;

import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import net.minecraft.world.entity.Entity;

public abstract class EntityActionType extends AbstractActionType<EntityActionContext, EntityAction> {

	@Override
	public void accept(EntityActionContext context) {

		Entity entity = context.entity();

		if (entity != null) {
			execute(entity);
		}

	}

	@Override
	public EntityAction createAction() {
		return new EntityAction(this);
	}

	protected abstract void execute(Entity entity);

}
