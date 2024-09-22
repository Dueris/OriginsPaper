package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FuelConditionType {

	public static boolean condition(ItemStack stack, Comparison comparison, int compareTo) {
		Integer fuelTime = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
		return comparison.compare(fuelTime == null ? 0 : fuelTime, compareTo);
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fuel"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
