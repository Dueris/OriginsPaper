package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.item.ItemPowersComponent;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PowerCountConditionType {

	public static boolean condition(@NotNull ItemStack stack, @Nullable EquipmentSlotGroup slot, Comparison comparison, int compareTo) {
		ItemPowersComponent itemPowers = new ItemPowersComponent(stack);
		int powers;

		if (slot != null) {
			powers = (int) itemPowers
				.stream()
				.filter(entry -> entry.slot().equals(slot))
				.count();
		} else {
			powers = itemPowers.size();
		}

		return comparison.compare(powers, compareTo);
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power_count"),
			new SerializableData()
				.add("slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT.optional(), Optional.empty())
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("slot"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
