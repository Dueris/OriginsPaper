package me.dueris.originspaper.action.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.action.ItemActionFactory;
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
