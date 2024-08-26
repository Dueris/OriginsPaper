package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ArmorValueCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		return worldAndStack.getB().getItem() instanceof ArmorItem armorItem
			&& comparison.compare(armorItem.getDefense(), compareTo);

	}

	public static @NotNull ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("armor_value"),
			SerializableData.serializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			ArmorValueCondition::condition
		);
	}

}
