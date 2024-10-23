package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.factory.ItemActionTypeFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DamageActionType {

	public static void action(Level world, ItemStack stack, int amount, boolean ignoreUnbreaking) {

		if (!(world instanceof ServerLevel serverWorld)) {
			return;
		}

		if (ignoreUnbreaking) {

			if (amount >= stack.getMaxDamage()) {
				stack.shrink(1);
			} else {
				stack.setDamageValue(stack.getDamageValue() + amount);
			}

		} else {
			stack.hurtAndBreak(amount, serverWorld, null, item -> {
			});
		}

	}

	public static ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("damage"),
			new SerializableData()
				.add("amount", SerializableDataTypes.INT, 1)
				.add("ignore_unbreaking", SerializableDataTypes.BOOLEAN, false),
			(data, worldAndStack) -> action(worldAndStack.getA(), worldAndStack.getB(),
				data.get("amount"),
				data.get("ignore_unbreaking")
			)
		);
	}

}
