package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmorValueConditionType {

	public static boolean condition(ItemStack stack, Comparison comparison, int compareTo) {

		int protection = stack.getItem() instanceof ArmorItem armorItem
			? armorItem.getDefense()
			: 0;

		return comparison.compare(protection, compareTo);

	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("armor_value"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
