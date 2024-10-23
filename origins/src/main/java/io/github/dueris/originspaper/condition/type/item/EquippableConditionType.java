package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EquippableConditionType {

	public static boolean condition(ItemStack stack, @Nullable EquipmentSlot slot) {
		Equipable equipment = Equipable.get(stack);
		return equipment != null
			&& (slot == null || slot == equipment.getEquipmentSlot());
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("equippable"),
			new SerializableData()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT, null),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("equipment_slot")
			)
		);
	}

}
