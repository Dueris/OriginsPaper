package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EmptyCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().isEmpty();
	}

	public static ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("empty"),
			InstanceDefiner.instanceDefiner(),
			EmptyCondition::condition
		);
	}

}
