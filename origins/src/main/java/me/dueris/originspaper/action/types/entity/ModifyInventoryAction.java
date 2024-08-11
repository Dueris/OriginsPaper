package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.InventoryType;
import me.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static me.dueris.originspaper.util.Util.modifyInventory;

public class ModifyInventoryAction {

	public static void action(@NotNull DeserializedFactoryJson data, Entity entity) {

		InventoryType inventoryType = data.get("inventory_type");
		Util.ProcessMode processMode = data.get("process_mode");
		int limit = data.getInt("limit");

		switch (inventoryType) {
			case INVENTORY:
				modifyInventory(data, entity, processMode.getProcessor(), limit);
				break;
		}
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_inventory"),
			InstanceDefiner.instanceDefiner()
				.add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
				.add("process_mode", ApoliDataTypes.PROCESS_MODE, Util.ProcessMode.STACKS)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", SerializableDataTypes.list(ApoliDataTypes.ITEM_SLOT), null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("limit", SerializableDataTypes.INT, 0),
			ModifyInventoryAction::action
		);
	}
}
