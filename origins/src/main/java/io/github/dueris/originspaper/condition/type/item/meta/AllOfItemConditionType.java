package io.github.dueris.originspaper.condition.type.item.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.context.ItemConditionContext;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllOfItemConditionType extends ItemConditionType implements AllOfMetaConditionType<ItemConditionContext, ItemCondition> {

	private final List<ItemCondition> conditions;

	public AllOfItemConditionType(List<ItemCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {
		return testConditions(new ItemConditionContext(world, stack));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.ALL_OF;
	}

	@Override
	public List<ItemCondition> conditions() {
		return conditions;
	}

}
