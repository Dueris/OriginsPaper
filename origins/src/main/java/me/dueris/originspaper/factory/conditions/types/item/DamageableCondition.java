package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DamageableCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().isDamageableItem();
	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.identifier("damageable"),
			InstanceDefiner.instanceDefiner(),
			DamageableCondition::condition
		);

	}

}
