package io.github.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.action.ItemActionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConsumeAction {

	public static @NotNull ActionTypeFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionTypeFactory.createItemStackBased(OriginsPaper.apoliIdentifier("consume"), SerializableData.serializableData()
				.add("amount", SerializableDataTypes.INT, 1),
			(data, worldAndStack) -> worldAndStack.getB().shrink(data.getInt("amount")));
	}
}
