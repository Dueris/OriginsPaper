package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RelativeDurabilityCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		Comparison comparison = data.get("comparison");
		float compareTo = data.get("compare_to");

		ItemStack stack = worldAndStack.getB();
		float relativeDurability = (float) Math.abs(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();

		return comparison.compare(relativeDurability, compareTo);

	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("relative_durability"),
			SerializableData.serializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			RelativeDurabilityCondition::condition
		);
	}

}
