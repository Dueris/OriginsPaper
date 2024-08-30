package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class SmeltableConditionType {

	public static boolean condition(Level world, ItemStack stack) {
		return world.getRecipeManager()
			.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), world)
			.isPresent();
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("smeltable"),
			new SerializableData(),
			(data, worldAndStack) -> condition(worldAndStack.getA(), worldAndStack.getB())
		);
	}

}
