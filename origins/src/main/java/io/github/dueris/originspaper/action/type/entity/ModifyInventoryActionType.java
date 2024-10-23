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
import net.minecraft.world.entity.LivingEntity;

import static io.github.dueris.originspaper.util.InventoryUtil.modifyInventory;


public class ModifyInventoryActionType {

	public static void action(SerializableData.Instance data, Entity entity) {

		InventoryUtil.InventoryType inventoryType = data.get("inventory_type");
		InventoryUtil.ProcessMode processMode = data.get("process_mode");
		int limit = data.getInt("limit");

		switch (inventoryType) {
			case INVENTORY:
				modifyInventory(data, entity, null, processMode.getProcessor(), limit);
				break;
			case POWER:
				if (!data.isPresent("power") || !(entity instanceof LivingEntity livingEntity)) return;

				Power targetPower = data.get("power");
				PowerType targetPowerType = PowerHolderComponent.KEY.get(livingEntity).getPowerType(targetPower);

				if (!(targetPowerType instanceof InventoryPowerType inventoryPower)) return;
				modifyInventory(data, livingEntity, inventoryPower, processMode.getProcessor(), limit);
				break;
		}
	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("modify_inventory"),
			new SerializableData()
				.add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryUtil.InventoryType.INVENTORY)
				.add("process_mode", ApoliDataTypes.PROCESS_MODE, InventoryUtil.ProcessMode.STACKS)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", ApoliDataTypes.ITEM_SLOTS, null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("power", ApoliDataTypes.POWER_REFERENCE, null)
				.add("limit", SerializableDataTypes.INT, 0),
			ModifyInventoryActionType::action
		);
	}

}
