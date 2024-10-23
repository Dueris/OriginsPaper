package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
import net.minecraft.world.item.ItemStack;

public class ConsumeActionType {

	public static void action(ItemStack stack, int amount) {
		stack.shrink(amount);
	}

	public static ItemActionTypeFactory getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("consume"),
			new SerializableData()
				.add("amount", SerializableDataTypes.INT, 1),
			(data, worldAndStack) -> action(worldAndStack.getB(),
				data.get("amount")
			)
		);
	}

}
