package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.AndMetaActionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.world.entity.Entity;

public class AndBiEntityActionType extends BiEntityActionType implements AndMetaActionType<BiEntityActionContext, BiEntityAction> {

	private final List<BiEntityAction> actions;

	public AndBiEntityActionType(List<BiEntityAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeActions(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.AND;
	}

	@Override
	public List<BiEntityAction> actions() {
		return actions;
	}

}
