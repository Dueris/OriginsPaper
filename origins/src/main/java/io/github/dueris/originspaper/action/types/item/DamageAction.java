package io.github.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.action.ItemActionTypeFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DamageAction {

	public static void action(SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		if (!(worldAndStack.getA() instanceof ServerLevel serverWorld)) {
			return;
		}

		ItemStack stack = worldAndStack.getB();
		int damageAmount = data.getInt("amount");

		if (data.getBoolean("ignore_unbreaking")) {

			if (damageAmount >= stack.getMaxDamage()) {
				stack.shrink(1);
			} else {
				stack.setDamageValue(stack.getDamageValue() + damageAmount);
			}

		} else {
			stack.hurtAndBreak(damageAmount, serverWorld, null, item -> {
			});
		}

	}

	public static @NotNull ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("damage"),
			SerializableData.serializableData()
				.add("amount", SerializableDataTypes.INT, 1)
				.add("ignore_unbreaking", SerializableDataTypes.BOOLEAN, false),
			DamageAction::action
		);
	}
}
