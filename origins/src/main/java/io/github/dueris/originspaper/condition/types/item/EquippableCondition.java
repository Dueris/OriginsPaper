package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EquippableCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		ItemStack stack = worldAndStack.getB();
		Equipable equipment = Equipable.get(stack);

		EquipmentSlot equipmentSlot = data.get("equipment_slot");
		return (equipmentSlot == null && equipment != null)
			|| (equipment != null && equipment.getEquipmentSlot() == equipmentSlot);

	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("equippable"),
			SerializableData.serializableData()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT, null),
			EquippableCondition::condition
		);

	}

}
