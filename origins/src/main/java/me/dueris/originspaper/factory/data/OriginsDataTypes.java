package me.dueris.originspaper.factory.data;

import com.google.gson.JsonPrimitive;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import me.dueris.originspaper.factory.data.types.Impact;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class OriginsDataTypes {

	public static final SerializableDataBuilder<ItemStack> ITEM_OR_ITEM_STACK = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
				return SerializableDataTypes.ITEM_STACK.deserialize(jsonElement);
			}

			Item item = SerializableDataTypes.ITEM.deserialize(jsonPrimitive);
			return new ItemStack(item);
		}, ItemStack.class
	);
	public static final SerializableDataBuilder<Impact> IMPACT = SerializableDataTypes.enumValue(Impact.class);
}
