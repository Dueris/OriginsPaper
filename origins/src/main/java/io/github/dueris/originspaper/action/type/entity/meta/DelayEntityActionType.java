package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.DelayMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DelayEntityActionType extends EntityActionType implements DelayMetaActionType<EntityActionContext, EntityAction> {

	private final EntityAction action;
	private final int ticks;

	public DelayEntityActionType(EntityAction action, int ticks) {
		this.action = action;
		this.ticks = ticks;
	}

	@Override
	protected void execute(Entity entity) {
		executeAction(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.DELAY;
	}

	@Override
	public EntityAction action() {
		return action;
	}

	@Override
	public int ticks() {
		return ticks;
	}

}
