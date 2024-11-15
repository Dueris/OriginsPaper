package io.github.dueris.originspaper.action.context;

import io.github.dueris.originspaper.condition.context.BiEntityConditionContext;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import net.minecraft.world.entity.Entity;

public record BiEntityActionContext(Entity actor,
									Entity target) implements TypeActionContext<BiEntityConditionContext> {

	@Override
	public BiEntityConditionContext forCondition() {
		return new BiEntityConditionContext(actor(), target());
	}

}
