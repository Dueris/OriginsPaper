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

public class RelativeDurabilityConditionType {

	public static boolean condition(ItemStack stack, Comparison comparison, float compareTo) {

		float durability = stack.isDamageableItem()
			? (float) (stack.getMaxDamage() - stack.getDamageValue()) / (float) stack.getMaxDamage()
			: 1.0F;

		return comparison.compare(durability, compareTo);

	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("relative_durability"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
