package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChoiceMetaActionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import org.jetbrains.annotations.NotNull;

public class ChoiceEntityActionType extends EntityActionType implements ChoiceMetaActionType<EntityActionContext, EntityAction> {

	private final ShufflingList<EntityAction> actions;

	public ChoiceEntityActionType(ShufflingList<EntityAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Entity entity) {
		executeActions(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.CHOICE;
	}

	@Override
	public ShufflingList<EntityAction> actions() {
		return actions;
	}

}
