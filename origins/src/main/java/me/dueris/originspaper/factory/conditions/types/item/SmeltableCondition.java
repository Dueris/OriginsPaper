package me.dueris.originspaper.factory.conditions.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class SmeltableCondition {

	public static boolean condition(DeserializedFactoryJson data, Pair<Level, ItemStack> worldAndStack) {
		Level world = worldAndStack.getA();
		return world != null && world.getRecipeManager()
			.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(worldAndStack.getB()), world)
			.isPresent();
	}

	public static ConditionFactory<Pair<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("smeltable"),
			InstanceDefiner.instanceDefiner(),
			SmeltableCondition::condition
		);
	}

}
