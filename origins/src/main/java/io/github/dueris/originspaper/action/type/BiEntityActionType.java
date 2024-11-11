package io.github.dueris.originspaper.action.type;

import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import net.minecraft.world.entity.Entity;

public abstract class BiEntityActionType extends AbstractActionType<BiEntityActionContext, BiEntityAction> {

	@Override
	public void accept(BiEntityActionContext context) {
		execute(context.actor(), context.target());
	}

	@Override
	public BiEntityAction createAction() {
		return new BiEntityAction(this);
	}

	protected abstract void execute(Entity actor, Entity target);

}
