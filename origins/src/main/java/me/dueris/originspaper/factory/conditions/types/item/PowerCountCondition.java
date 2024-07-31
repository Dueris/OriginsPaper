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

public class PowerCountCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {

		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");
		int total = 0;

		return comparison.compare(total, compareTo);

	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("power_count"),
			InstanceDefiner.instanceDefiner()
				.add("slot", SerializableDataTypes.EQUIPMENT_SLOT, null)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			PowerCountCondition::condition
		);
	}

}
