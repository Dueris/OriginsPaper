package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

// TODO - PowerTypes in ItemStacks - Dueris
public class PowerCountConditionType {

	public static boolean condition(ItemStack stack, @Nullable EquipmentSlotGroup slot, Comparison comparison, int compareTo) {
		return comparison.compare(0, compareTo);

	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power_count"),
			new SerializableData()
				.add("slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT)
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
