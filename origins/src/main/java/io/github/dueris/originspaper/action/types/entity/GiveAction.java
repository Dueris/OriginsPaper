package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GiveAction {

	public static void action(SerializableData.Instance data, @NotNull Entity entity) {

		if (entity.level().isClientSide) {
			return;
		}

		ItemStack stack = data.<ItemStack>get("stack").copy();
		if (stack.isEmpty()) {
			return;
		}

		SlotAccess stackReference = Util.createStackReference(stack);
		if (data.isPresent("item_action")) {
			Consumer<Tuple<Level, SlotAccess>> itemAction = data.get("item_action");
			itemAction.accept(new Tuple<>(entity.level(), stackReference));
		}

		stack = stackReference.get();

		tryPreferredSlot:
		if (data.isPresent("preferred_slot") && entity instanceof LivingEntity livingEntity) {

			EquipmentSlot preferredSlot = data.get("preferred_slot");
			ItemStack stackInSlot = livingEntity.getItemBySlot(preferredSlot);

			if (stackInSlot.isEmpty()) {
				livingEntity.setItemSlot(preferredSlot, stack);
				return;
			}

			if (!ItemStack.matches(stackInSlot, stack) || stackInSlot.getCount() >= stackInSlot.getMaxStackSize()) {
				break tryPreferredSlot;
			}

			int itemsToGive = Math.min(stackInSlot.getMaxStackSize() - stackInSlot.getCount(), stack.getCount());

			stackInSlot.grow(itemsToGive);
			stack.shrink(itemsToGive);

			if (stack.isEmpty()) {
				return;
			}

		}

		if (entity instanceof Player playerEntity) {
			playerEntity.getInventory().placeItemBackInInventory(stack);
		} else {
			Util.throwItem(entity, stack, false, false);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("give"),
			SerializableData.serializableData()
				.add("stack", SerializableDataTypes.ITEM_STACK)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("preferred_slot", SerializableDataTypes.EQUIPMENT_SLOT, null),
			GiveAction::action
		);
	}
}
