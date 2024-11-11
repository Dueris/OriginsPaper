package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.SideMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SideBiEntityActionType extends BiEntityActionType implements SideMetaActionType<BiEntityActionContext, BiEntityAction> {

	private final BiEntityAction action;
	private final Side side;

	public SideBiEntityActionType(BiEntityAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeAction(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.SIDE;
	}

	@Override
	public BiEntityAction action() {
		return action;
	}

	@Override
	public Side side() {
		return side;
	}

}
