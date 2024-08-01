package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EquippableCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Level, ItemStack> worldAndStack) {

		ItemStack stack = worldAndStack.getB();
		Equipable equipment = Equipable.get(stack);

		EquipmentSlot equipmentSlot = data.get("equipment_slot");
		return (equipmentSlot == null && equipment != null)
			|| (equipment != null && equipment.getEquipmentSlot() == equipmentSlot);

	}

	public static ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("equippable"),
			InstanceDefiner.instanceDefiner()
				.add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT, null),
			EquippableCondition::condition
		);

	}

}
