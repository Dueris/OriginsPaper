package io.github.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.action.ItemActionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConsumeAction {

	public static @NotNull ActionFactory<Tuple<Level, SlotAccess>> getFactory() {
		return ItemActionFactory.createItemStackBased(OriginsPaper.apoliIdentifier("consume"), InstanceDefiner.instanceDefiner()
				.add("amount", SerializableDataTypes.INT, 1),
			(data, worldAndStack) -> worldAndStack.getB().shrink(data.getInt("amount")));
	}
}
