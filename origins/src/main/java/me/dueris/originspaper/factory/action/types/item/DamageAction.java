package me.dueris.originspaper.factory.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.action.ItemActionFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DamageAction {

	public static void action(DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

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

	public static @NotNull ActionFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionFactory.createItemStackBased(
			OriginsPaper.apoliIdentifier("damage"),
			InstanceDefiner.instanceDefiner()
				.add("amount", SerializableDataTypes.INT, 1)
				.add("ignore_unbreaking", SerializableDataTypes.BOOLEAN, false),
			DamageAction::action
		);
	}
}
