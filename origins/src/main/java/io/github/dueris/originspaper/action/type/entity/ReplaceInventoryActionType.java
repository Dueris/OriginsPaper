package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.InventoryType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.dueris.originspaper.util.Util.replaceInventory;

public class ReplaceInventoryActionType {

	public static void action(SerializableData.@NotNull Instance data, Entity entity) {

		InventoryType inventoryType = data.get("inventory_type");

		if (Objects.requireNonNull(inventoryType) == InventoryType.INVENTORY) {
			replaceInventory(data, entity);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("replace_inventory"),
			new SerializableData()
				.add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", SerializableDataType.of(ApoliDataTypes.ITEM_SLOT.listOf()), null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("power", ApoliDataTypes.POWER_REFERENCE, null)
				.add("stack", SerializableDataTypes.ITEM_STACK)
				.add("merge_nbt", SerializableDataTypes.BOOLEAN, false),
			ReplaceInventoryActionType::action
		);
	}

}
