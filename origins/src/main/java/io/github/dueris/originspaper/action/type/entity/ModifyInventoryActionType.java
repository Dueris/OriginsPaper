package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.InventoryType;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

import static io.github.dueris.originspaper.util.Util.modifyInventory;

public class ModifyInventoryActionType {

	public static void action(SerializableData.Instance data, Entity entity) {

		InventoryType inventoryType = data.get("inventory_type");
		Util.ProcessMode processMode = data.get("process_mode");
		int limit = data.getInt("limit");

		if (Objects.requireNonNull(inventoryType) == InventoryType.INVENTORY) {
			modifyInventory(data, entity, processMode.getProcessor(), limit);
		}
	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("modify_inventory"),
			new SerializableData()
				.add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
				.add("process_mode", ApoliDataTypes.PROCESS_MODE, Util.ProcessMode.STACKS)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", SerializableDataType.of(ApoliDataTypes.ITEM_SLOT.listOf()), null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("power", ApoliDataTypes.POWER_REFERENCE, null)
				.add("limit", SerializableDataTypes.INT, 0),
			ModifyInventoryActionType::action
		);
	}

}
