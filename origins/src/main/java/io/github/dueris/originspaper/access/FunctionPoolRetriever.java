package io.github.dueris.originspaper.access;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;
import java.util.function.BiFunction;

public interface FunctionPoolRetriever {
	BiFunction<ItemStack, LootContext, ItemStack> apoli$getCompositeFunction();

	List<LootPool> apoli$getPools();
}
