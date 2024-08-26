package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CustomDataCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).matchedBy(data.get("nbt"));
	}

	public static @NotNull ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("custom_data"),
			SerializableData.serializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			CustomDataCondition::condition
		);

	}

}
