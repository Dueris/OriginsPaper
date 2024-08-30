package io.github.dueris.originspaper.condition.type.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class FoodConditionType {

	public static boolean condition(ItemStack stack) {
		return stack.has(DataComponents.FOOD);
	}

}
