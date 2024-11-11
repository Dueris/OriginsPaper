package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChoiceMetaActionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import org.jetbrains.annotations.NotNull;

public class ChoiceBiEntityActionType extends BiEntityActionType implements ChoiceMetaActionType<BiEntityActionContext, BiEntityAction> {

	private final ShufflingList<BiEntityAction> actions;

	public ChoiceBiEntityActionType(ShufflingList<BiEntityAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeActions(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.CHOICE;
	}

	@Override
	public ShufflingList<BiEntityAction> actions() {
		return actions;
	}

}
