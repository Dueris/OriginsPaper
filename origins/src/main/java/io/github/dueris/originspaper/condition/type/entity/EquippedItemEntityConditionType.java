package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EquippedItemEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<EquippedItemEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("item_condition", ItemCondition.DATA_TYPE)
			.add("equipment_slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT),
		data -> new EquippedItemEntityConditionType(
			data.get("item_condition"),
			data.get("equipment_slot")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("item_condition", conditionType.itemCondition)
			.set("equipment_slot", conditionType.equipmentSlot)
	);

	private final ItemCondition itemCondition;
	private final EquipmentSlotGroup equipmentSlot;

	public EquippedItemEntityConditionType(ItemCondition itemCondition, EquipmentSlotGroup equipmentSlot) {
		this.itemCondition = itemCondition;
		this.equipmentSlot = equipmentSlot;
	}

	@Override
	public boolean test(Entity entity) {

		if (!(entity instanceof LivingEntity livingEntity)) {
			return false;
		}

		for (EquipmentSlot slot : EquipmentSlot.values()) {

			if (equipmentSlot.test(slot) && itemCondition.test(livingEntity.level(), livingEntity.getItemBySlot(slot))) {
				return true;
			}

		}

		return false;

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.EQUIPPED_ITEM;
	}

}
