package io.github.dueris.originspaper.condition;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.condition.context.ItemConditionContext;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ItemCondition extends AbstractCondition<ItemConditionContext, ItemConditionType> {

	public static final SerializableDataType<ItemCondition> DATA_TYPE = SerializableDataType.lazy(() -> ApoliDataTypes.condition("type", ItemConditionTypes.DATA_TYPE, ItemCondition::new));

	public ItemCondition(ItemConditionType conditionType, boolean inverted) {
		super(conditionType, inverted);
	}

	public ItemCondition(ItemConditionType conditionType) {
		this(conditionType, false);
	}

	public boolean test(Level world, ItemStack stack) {
		return test(new ItemConditionContext(world, stack));
	}

}
