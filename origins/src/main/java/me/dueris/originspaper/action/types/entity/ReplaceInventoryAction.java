package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.InventoryType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static me.dueris.originspaper.util.Util.replaceInventory;

public class ReplaceInventoryAction {

	public static void action(@NotNull DeserializedFactoryJson data, Entity entity) {

		InventoryType inventoryType = data.get("inventory_type");

		switch (inventoryType) {
			case INVENTORY -> replaceInventory(data, entity);
		}

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("replace_inventory"),
			InstanceDefiner.instanceDefiner()
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
