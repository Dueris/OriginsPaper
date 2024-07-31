package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireResistantCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().has(DataComponents.FIRE_RESISTANT);
	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fire_resistant"),
			InstanceDefiner.instanceDefiner(),
			FireResistantCondition::condition
		);

	}

}
