package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.InventoryType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static io.github.dueris.originspaper.util.Util.replaceInventory;

public class ReplaceInventoryAction {

	public static void action(@NotNull SerializableData.Instance data, Entity entity) {

		InventoryType inventoryType = data.get("inventory_type");

		switch (inventoryType) {
			case INVENTORY -> replaceInventory(data, entity);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("replace_inventory"),
			SerializableData.serializableData()
				.add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", SerializableDataTypes.list(ApoliDataTypes.ITEM_SLOT), null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("stack", SerializableDataTypes.ITEM_STACK)
				.add("merge_nbt", SerializableDataTypes.BOOLEAN, false),
			ReplaceInventoryAction::action
		);
	}
}
