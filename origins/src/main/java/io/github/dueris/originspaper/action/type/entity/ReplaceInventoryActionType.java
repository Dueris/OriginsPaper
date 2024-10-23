package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.type.InventoryPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.world.entity.Entity;

import static io.github.dueris.originspaper.util.InventoryUtil.replaceInventory;

public class ReplaceInventoryActionType {

	public static void action(SerializableData.Instance data, Entity entity) {

		InventoryUtil.InventoryType inventoryType = data.get("inventory_type");

		switch (inventoryType) {
			case INVENTORY -> replaceInventory(data, entity, null);
			case POWER -> {

				if (!data.isPresent("power")) return;
				PowerHolderComponent.KEY.maybeGet(entity).ifPresent(
					powerHolderComponent -> {

						Power targetPower = data.get("power");
						PowerType targetPowerType = powerHolderComponent.getPowerType(targetPower);
						if (!(targetPowerType instanceof InventoryPowerType inventoryPower)) return;

						replaceInventory(data, entity, inventoryPower);

					}
				);

			}
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("replace_inventory"),
			new SerializableData()
				.add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryUtil.InventoryType.INVENTORY)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", ApoliDataTypes.ITEM_SLOTS, null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("power", ApoliDataTypes.POWER_REFERENCE, null)
				.add("stack", SerializableDataTypes.ITEM_STACK)
				.add("merge_nbt", SerializableDataTypes.BOOLEAN, false),
			ReplaceInventoryActionType::action
		);
	}

}
