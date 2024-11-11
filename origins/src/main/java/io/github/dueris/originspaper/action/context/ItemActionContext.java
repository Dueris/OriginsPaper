package io.github.dueris.originspaper.action.context;

import io.github.dueris.originspaper.condition.context.ItemConditionContext;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;

public record ItemActionContext(Level world, SlotAccess stackReference) implements TypeActionContext<ItemConditionContext> {

	@Override
	public ItemConditionContext forCondition() {
		return new ItemConditionContext(world(), stackReference().get());
	}

}
