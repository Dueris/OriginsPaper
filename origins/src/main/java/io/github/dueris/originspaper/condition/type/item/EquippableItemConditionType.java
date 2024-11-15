package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EquippableItemConditionType extends ItemConditionType {

	public static final TypedDataObjectFactory<EquippableItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("equipment_slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT.optional(), Optional.empty()),
		data -> new EquippableItemConditionType(
			data.get("equipment_slot")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("equipment_slot", conditionType.equipmentSlot)
	);

	private final Optional<EquipmentSlotGroup> equipmentSlot;

	public EquippableItemConditionType(Optional<EquipmentSlotGroup> equipmentSlot) {
		this.equipmentSlot = equipmentSlot;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {
		Equipable equipment = Equipable.get(stack);
		return equipment != null
			&& equipmentSlot.map(slot -> slot.test(equipment.getEquipmentSlot())).orElse(true);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.EQUIPPABLE;
	}

}
