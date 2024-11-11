package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.DelayMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DelayBiEntityActionType extends BiEntityActionType implements DelayMetaActionType<BiEntityActionContext, BiEntityAction> {

	private final BiEntityAction action;
	private final int ticks;

	public DelayBiEntityActionType(BiEntityAction action, int ticks) {
		this.action = action;
		this.ticks = ticks;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeAction(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.DELAY;
	}

	@Override
	public BiEntityAction action() {
		return action;
	}

	@Override
	public int ticks() {
		return ticks;
	}

}
