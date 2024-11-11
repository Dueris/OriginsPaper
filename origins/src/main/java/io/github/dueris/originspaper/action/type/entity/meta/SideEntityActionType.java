package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.SideMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SideEntityActionType extends EntityActionType implements SideMetaActionType<EntityActionContext, EntityAction> {

	private final EntityAction action;
	private final Side side;

	public SideEntityActionType(EntityAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	protected void execute(Entity entity) {
		executeAction(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SIDE;
	}

	@Override
	public EntityAction action() {
		return action;
	}

	@Override
	public Side side() {
		return side;
	}

}
