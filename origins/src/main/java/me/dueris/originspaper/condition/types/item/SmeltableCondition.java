package me.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SmeltableCondition {

	public static boolean condition(DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		Level world = worldAndStack.getA();
		return world != null && world.getRecipeManager()
			.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(worldAndStack.getB()), world)
			.isPresent();
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("smeltable"),
			InstanceDefiner.instanceDefiner(),
			SmeltableCondition::condition
		);
	}

}
