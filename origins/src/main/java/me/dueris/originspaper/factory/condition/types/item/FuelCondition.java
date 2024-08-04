package me.dueris.originspaper.factory.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;

public class FuelCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		Integer fuelTime = AbstractFurnaceBlockEntity.getFuel().get(worldAndStack.getB().getItem().asItem());
		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		return fuelTime != null
			&& comparison.compare(fuelTime, compareTo);

	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fuel"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0),
			FuelCondition::condition
		);
	}

}
