package io.github.dueris.originspaper.condition.type;

import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.context.ItemConditionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ItemConditionType extends AbstractConditionType<ItemConditionContext, ItemCondition> {

	@Override
	public boolean test(ItemConditionContext context) {
		return test(context.world(), context.stack());
	}

	@Override
	public ItemCondition createCondition(boolean inverted) {
		return new ItemCondition(this, inverted);
	}

	public abstract boolean test(Level world, ItemStack stack);

}
