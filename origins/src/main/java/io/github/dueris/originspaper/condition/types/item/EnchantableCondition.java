package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnchantableCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().isEnchantable();
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("enchantable"),
			SerializableData.serializableData(),
			EnchantableCondition::condition
		);
	}

}
