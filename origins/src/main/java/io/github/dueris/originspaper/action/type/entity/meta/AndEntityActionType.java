package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.AndMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndEntityActionType extends EntityActionType implements AndMetaActionType<EntityActionContext, EntityAction> {

	private final List<EntityAction> actions;

	public AndEntityActionType(List<EntityAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Entity entity) {
		executeActions(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.AND;
	}

	@Override
	public List<EntityAction> actions() {
		return actions;
	}

}
