package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FuelCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {

		Integer fuelTime = AbstractFurnaceBlockEntity.getFuel().get(worldAndStack.getB().getItem().asItem());
		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		return fuelTime != null
			&& comparison.compare(fuelTime, compareTo);

	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fuel"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0),
			FuelCondition::condition
		);
	}

}
