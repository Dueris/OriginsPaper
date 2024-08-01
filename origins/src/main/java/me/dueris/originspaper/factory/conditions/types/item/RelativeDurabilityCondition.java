package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RelativeDurabilityCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Level, ItemStack> worldAndStack) {

		Comparison comparison = data.get("comparison");
		float compareTo = data.get("compare_to");

		ItemStack stack = worldAndStack.getB();
		float relativeDurability = (float) Math.abs(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();

		return comparison.compare(relativeDurability, compareTo);

	}

	public static ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("relative_durability"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			RelativeDurabilityCondition::condition
		);
	}

}
