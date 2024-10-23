package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.type.InventoryPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class InventoryConditionType {

	public static boolean condition(SerializableData.@NotNull Instance data, Entity entity) {

		Set<InventoryUtil.InventoryType> inventoryTypes = data.get("inventory_types");
		InventoryUtil.ProcessMode processMode = data.get("process_mode");
		Comparison comparison = data.get("comparison");

		int compareTo = data.get("compare_to");
		int matches = 0;

		if (inventoryTypes.contains(InventoryUtil.InventoryType.INVENTORY)) {
			matches += InventoryUtil.checkInventory(data, entity, null, processMode.getProcessor());
		}

		powerTest:
		if (inventoryTypes.contains(InventoryUtil.InventoryType.POWER)) {

			PowerHolderComponent component = PowerHolderComponent.KEY.maybeGet(entity).orElse(null);
			if (component == null) {
				break powerTest;
			}

			Power targetPower = data.get("power");
			if (targetPower == null) {
				break powerTest;
			}

			PowerType targetPowerType = component.getPowerType(targetPower);
			if (!(targetPowerType instanceof InventoryPowerType inventoryPower)) {
				break powerTest;
			}

			matches += InventoryUtil.checkInventory(data, entity, inventoryPower, processMode.getProcessor());

		}

		return comparison.compare(matches, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("inventory"),
			new SerializableData()
				.add("inventory_types", ApoliDataTypes.INVENTORY_TYPE_SET, EnumSet.of(InventoryUtil.InventoryType.INVENTORY))
				.add("process_mode", ApoliDataTypes.PROCESS_MODE, InventoryUtil.ProcessMode.ITEMS)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", ApoliDataTypes.ITEM_SLOTS, null)
				.add("slot", ApoliDataTypes.ITEM_SLOT, null)
				.add("power", ApoliDataTypes.POWER_REFERENCE, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0),
			InventoryConditionType::condition
		);
	}

}
