package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GiveActionType {

	public static void action(Entity entity, ItemStack newStack, Consumer<Tuple<Level, SlotAccess>> itemAction, @Nullable EquipmentSlot preferredSlot) {

		if (entity.level().isClientSide || newStack.isEmpty()) {
			return;
		}

		SlotAccess stackReference = InventoryUtil.createStackReference(newStack);
		itemAction.accept(new Tuple<>(entity.level(), stackReference));

		ItemStack stackToGive = stackReference.get();

		tryPreferredSlot:
		if (preferredSlot != null && entity instanceof LivingEntity living) {

			ItemStack stackInSlot = living.getItemBySlot(preferredSlot);
			if (stackInSlot.isEmpty()) {
				living.setItemSlot(preferredSlot, stackToGive);
				return;
			}

			if (!ItemStack.matches(stackInSlot, stackToGive) || stackInSlot.getCount() >= stackInSlot.getMaxStackSize()) {
				break tryPreferredSlot;
			}

			int itemsToGive = Math.min(stackInSlot.getMaxStackSize() - stackInSlot.getCount(), stackToGive.getCount());

			stackInSlot.grow(itemsToGive);
			stackToGive.shrink(itemsToGive);

			if (stackToGive.isEmpty()) {
				return;
			}

		}

		if (entity instanceof Player player) {
			player.getInventory().placeItemBackInInventory(stackToGive);
		} else {
			InventoryUtil.throwItem(entity, stackToGive, false, false);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("give"),
			new SerializableData()
				.add("stack", SerializableDataTypes.ITEM_STACK)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("preferred_slot", SerializableDataTypes.EQUIPMENT_SLOT, null),
			(data, entity) -> action(entity,
				data.<ItemStack>get("stack").copy(),
				data.getOrElse("item_action", wsr -> {
				}),
				data.get("preferred_slot")
			)
		);
	}

}
