package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SmeltableCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		Level world = worldAndStack.getA();
		return world != null && world.getRecipeManager()
			.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(worldAndStack.getB()), world)
			.isPresent();
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("smeltable"),
			SerializableData.serializableData(),
			SmeltableCondition::condition
		);
	}

}
