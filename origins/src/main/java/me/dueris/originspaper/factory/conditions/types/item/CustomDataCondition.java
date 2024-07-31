package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class CustomDataCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).matchedBy(data.get("nbt"));
	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.identifier("nbt"),
			InstanceDefiner.instanceDefiner()
				.add("nbt", SerializableDataTypes.NBT),
			CustomDataCondition::condition
		);

	}

}
